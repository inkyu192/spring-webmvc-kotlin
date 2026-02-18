package spring.webmvc.infrastructure.external.s3

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CopyObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import spring.webmvc.infrastructure.exception.FailedAwsIntegrationException
import spring.webmvc.infrastructure.properties.AppProperties
import java.util.*

@Component
class S3Service(
    private val s3Client: S3Client,
    appProperties: AppProperties,
) {
    private val bucket = appProperties.aws.s3.bucket
    private val logger = LoggerFactory.getLogger(javaClass)

    fun putObject(file: MultipartFile): String {
        val filename = requireNotNull(file.originalFilename)
        val key = "temp/${UUID.randomUUID()}.${extractExtension(filename)}"

        val request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(file.contentType)
            .build()

        try {
            s3Client.putObject(request, RequestBody.fromInputStream(file.inputStream, file.size))
        } catch (e: Exception) {
            logger.error("Failed to put object to S3", e)
            throw FailedAwsIntegrationException(serviceName = "S3", throwable = e)
        }

        return key
    }

    fun copyObject(
        sourceKey: String,
        fileType: FileType,
        id: Long,
    ): String {
        val fileName = sourceKey.substringAfterLast('/')
        val destinationKey = "data/${fileType.path}/$id/$fileName"

        val copyRequest = CopyObjectRequest.builder()
            .sourceBucket(bucket)
            .sourceKey(sourceKey)
            .destinationBucket(bucket)
            .destinationKey(destinationKey)
            .build()

        try {
            s3Client.copyObject(copyRequest)
        } catch (e: Exception) {
            logger.error("Failed to copy object to S3", e)
            throw FailedAwsIntegrationException(serviceName = "S3", throwable = e)
        }

        return destinationKey
    }

    private fun extractExtension(filename: String): String {
        val lastDot = filename.lastIndexOf('.')

        return if (lastDot == -1 || lastDot == filename.length - 1) {
            "bin"
        } else {
            filename.substring(lastDot + 1)
        }
    }
}
