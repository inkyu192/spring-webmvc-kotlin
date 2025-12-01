package spring.webmvc.infrastructure.external

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CopyObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import spring.webmvc.domain.model.enums.FileType
import spring.webmvc.presentation.exception.AwsIntegrationException
import java.net.URLConnection
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class S3Service(
    private val s3Client: S3Client,
    @Value("\${aws.s3.bucket}")
    private val bucket: String,
) {
    private val logger = LoggerFactory.getLogger(S3Service::class.java)

    fun putObject(fileType: FileType, file: MultipartFile): String {
        validateFile(fileType, file)

        val filename = requireNotNull(file.originalFilename)
        val key = generateKey(directory = fileType.directory, filename = filename)
        val contentType = URLConnection.guessContentTypeFromName(filename)

        val request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(contentType)
            .build()

        runCatching {
            s3Client.putObject(request, RequestBody.fromInputStream(file.inputStream, file.size))
        }.onFailure { throwable ->
            logger.error("Failed to put object to S3", throwable)
            throw AwsIntegrationException(serviceName = "S3", throwable = throwable)
        }

        return key
    }

    fun copyObject(sourceKey: String, destinationType: FileType) {
        val destinationKey = replaceDirectory(sourceKey = sourceKey, destinationDirectory = destinationType.directory)

        val copyRequest = CopyObjectRequest.builder()
            .sourceBucket(bucket)
            .sourceKey(sourceKey)
            .destinationBucket(bucket)
            .destinationKey(destinationKey)
            .build()

        runCatching {
            s3Client.copyObject(copyRequest)
        }.onFailure { throwable ->
            logger.error("Failed to copy object to S3", throwable)
            throw AwsIntegrationException(serviceName = "S3", throwable = throwable)
        }
    }

    private fun validateFile(fileType: FileType, file: MultipartFile) {
        val filename = file.originalFilename
            ?: throw IllegalArgumentException("파일 이름이 존재하지 않습니다.")

        if (!filename.contains(".")) {
            throw IllegalArgumentException("확장자가 없는 파일입니다.")
        }

        val extension = extractExtension(filename)
        if (!fileType.allowedExtensions.contains(extension)) {
            throw IllegalArgumentException("허용되지 않은 확장자입니다: $extension")
        }

        if (file.size > fileType.maxSize) {
            throw IllegalArgumentException("파일 크기가 허용된 범위를 초과했습니다.")
        }
    }

    private fun generateKey(directory: String, filename: String): String {
        val extension = extractExtension(filename)
        val localDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val uuid = UUID.randomUUID().toString()

        return "$directory/$localDate/$uuid.$extension"
    }

    private fun extractExtension(filename: String): String {
        val lastDot = filename.lastIndexOf('.')
        return if (lastDot == -1 || lastDot == filename.length - 1) {
            "bin"
        } else {
            filename.substring(lastDot + 1)
        }
    }

    private fun replaceDirectory(sourceKey: String, destinationDirectory: String): String {
        val parts = sourceKey.split("/", limit = 2)
        if (parts.size != 2) {
            throw IllegalArgumentException("Invalid sourceKey format: $sourceKey")
        }
        return "${destinationDirectory}/${parts[1]}"
    }
}