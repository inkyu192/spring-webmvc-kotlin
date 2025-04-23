package spring.webmvc.infrastructure.external

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.testcontainers.containers.localstack.LocalStackContainer
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import spring.webmvc.infrastructure.config.LocalStackTestConfig

class S3ServiceTest : DescribeSpec({
    val bucket = "my-bucket"
    lateinit var s3Client: S3Client
    lateinit var s3Service: S3Service

    beforeSpec {
        s3Client = S3Client.builder()
            .endpointOverride(LocalStackTestConfig.localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3))
            .region(Region.of(LocalStackTestConfig.localStackContainer.region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        LocalStackTestConfig.localStackContainer.accessKey,
                        LocalStackTestConfig.localStackContainer.secretKey
                    )
                )
            )
            .build()

        s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build())
        s3Service = S3Service(s3Client)
    }

    describe("S3Service") {
        it("MultipartFile S3 업로드 후 key 반환한다") {
            val directory = "directory"
            val filename = "file.txt"
            val content = "content"

            val multipartFile = MockMultipartFile(
                filename, filename, MediaType.TEXT_PLAIN_VALUE, content.toByteArray(Charsets.UTF_8)
            )

            val key = s3Service.putObject(bucket = bucket, directory = directory, file = multipartFile)

            val response = s3Client.getObject(
                GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build()
            )

            val downloaded = response.readAllBytes().toString(Charsets.UTF_8)
            downloaded shouldBe content
        }
    }
})
