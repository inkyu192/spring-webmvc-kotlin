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
import spring.webmvc.infrastructure.common.FileType
import spring.webmvc.infrastructure.config.LocalStackTestContainerConfig

class S3ServiceTest : DescribeSpec({
    val bucket = "my-bucket"
    lateinit var s3Client: S3Client
    lateinit var s3Service: S3Service

    beforeSpec {
        s3Client = S3Client.builder()
            .endpointOverride(LocalStackTestContainerConfig.localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3))
            .region(Region.of(LocalStackTestContainerConfig.localStackContainer.region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        LocalStackTestContainerConfig.localStackContainer.accessKey,
                        LocalStackTestContainerConfig.localStackContainer.secretKey
                    )
                )
            )
            .build()

        s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build())
        s3Service = S3Service(s3Client, bucket)
    }

    describe("putObject") {
        it("MultipartFile S3 업로드 후 key 반환한다") {
            val filename = "file.txt"
            val content = "content"

            val multipartFile = MockMultipartFile(
                filename,
                filename,
                MediaType.TEXT_PLAIN_VALUE,
                content.toByteArray(Charsets.UTF_8)
            )

            val key = s3Service.putObject(fileType = FileType.TEMP, file = multipartFile)

            val response = s3Client.getObject(
                GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build()
            )

            val result = response.readAllBytes().toString(Charsets.UTF_8)

            result shouldBe content
        }
    }

    describe("copyObject") {
        it("S3 객체를 복사한다") {
            val filename = "file.txt"
            val content = "content"

            val multipartFile = MockMultipartFile(
                filename,
                filename,
                MediaType.TEXT_PLAIN_VALUE,
                content.toByteArray(Charsets.UTF_8)
            )

            val sourceKey = s3Service.putObject(fileType = FileType.TEMP, file = multipartFile)

            s3Service.copyObject(sourceKey = sourceKey, destinationType = FileType.PROFILE)

            val response = s3Client.getObject(
                GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(sourceKey)
                    .build()
            )

            String(response.readAllBytes(), Charsets.UTF_8) shouldBe content
        }
    }
})
