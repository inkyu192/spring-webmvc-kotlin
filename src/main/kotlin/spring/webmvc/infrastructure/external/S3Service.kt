package spring.webmvc.infrastructure.external

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CopyObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import spring.webmvc.infrastructure.common.FileType
import spring.webmvc.infrastructure.common.FileUtil
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
        val filename = requireNotNull(file.originalFilename)
        val key = generateKey(directory = filename, filename = fileType.directory)
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

    private fun generateKey(directory: String, filename: String): String {
        val extension = FileUtil.extractExtension(filename)
        val localDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val uuid = UUID.randomUUID().toString()

        return "$directory/$localDate/$uuid.$extension"
    }

    private fun replaceDirectory(sourceKey: String, destinationDirectory: String): String {
        val parts = sourceKey.split("/", limit = 2)
        if (parts.size != 2) {
            throw IllegalArgumentException("Invalid sourceKey format: $sourceKey")
        }
        return "${destinationDirectory}/${parts[1]}"
    }
}