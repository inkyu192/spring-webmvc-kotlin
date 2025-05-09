package spring.webmvc.infrastructure.external

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import spring.webmvc.presentation.exception.AwsIntegrationException
import java.net.URLConnection
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class S3Service(
    private val s3Client: S3Client,
) {
    private val logger = LoggerFactory.getLogger(S3Service::class.java)

    fun putObject(bucket: String, directory: String, file: MultipartFile): String {
        val filename = requireNotNull(file.originalFilename)
        val key = generateKey(directory, filename)
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

    private fun generateKey(directory: String, filename: String): String {
        val extension = extractExtension(filename)
        val localDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val uuid: String = UUID.randomUUID().toString()

        return "$directory/$localDate/$uuid.$extension"
    }

    private fun extractExtension(filename: String): String {
        val lastDot = filename.lastIndexOf('.')
        if (lastDot == -1 || lastDot == filename.length - 1) {
            return "bin"
        }
        return filename.substring(lastDot + 1)
    }
}