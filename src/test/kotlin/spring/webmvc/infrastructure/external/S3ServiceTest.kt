package spring.webmvc.infrastructure.external

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.*
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.testcontainers.containers.localstack.LocalStackContainer
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import spring.webmvc.infrastructure.config.LocalStackTestContainerConfig
import spring.webmvc.infrastructure.external.s3.FileType
import spring.webmvc.infrastructure.external.s3.S3Service
import spring.webmvc.infrastructure.properties.AppProperties

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class S3ServiceTest {
    private val bucket = "my-bucket"
    private lateinit var s3Client: S3Client
    private lateinit var s3Service: S3Service

    @BeforeAll
    fun setUpAll() {
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

        val endpoint = LocalStackTestContainerConfig
            .localStackContainer
            .getEndpointOverride(LocalStackContainer.Service.S3)
            .toString()

        val appProperties = mockk<AppProperties> {
            every { aws.s3 } returns AppProperties.AwsProperties.S3Properties(endpoint = endpoint, bucket = bucket)
            every { aws.cloudfront } returns AppProperties.AwsProperties.CloudFrontProperties(domain = "$endpoint/$bucket")
        }

        s3Service = S3Service(s3Client, appProperties)
    }

    @AfterEach
    fun tearDown() {
        val listResponse = s3Client.listObjectsV2(
            ListObjectsV2Request.builder()
                .bucket(bucket)
                .build()
        )

        listResponse.contents().forEach {
            s3Client.deleteObject(
                DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(it.key())
                    .build()
            )
        }
    }

    @Test
    @DisplayName("MultipartFile S3 업로드 후 key 반환한다")
    fun putObject() {
        val filename = "file.jpg"
        val content = "content"

        val multipartFile = MockMultipartFile(
            filename,
            filename,
            MediaType.IMAGE_JPEG_VALUE,
            content.toByteArray(Charsets.UTF_8)
        )

        val key = s3Service.putObject(file = multipartFile)

        val response = s3Client.getObject(
            GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build()
        )

        val result = response.readAllBytes().toString(Charsets.UTF_8)

        Assertions.assertThat(result).isEqualTo(content)
    }

    @Test
    @DisplayName("S3 객체를 복사한다")
    fun copyObject() {
        val filename = "file.jpg"
        val content = "content"

        val multipartFile = MockMultipartFile(
            filename,
            filename,
            MediaType.IMAGE_JPEG_VALUE,
            content.toByteArray(Charsets.UTF_8)
        )

        val sourceKey = s3Service.putObject(file = multipartFile)
        val id = 123L

        val destinationKey = s3Service.copyObject(sourceKey = sourceKey, fileType = FileType.PROFILE, id = id)

        val fileName = sourceKey.substringAfterLast('/')
        val expectedDestinationKey = "data/${FileType.PROFILE.path}/$id/$fileName"
        Assertions.assertThat(destinationKey).isEqualTo(expectedDestinationKey)

        val response = s3Client.getObject(
            GetObjectRequest.builder()
                .bucket(bucket)
                .key(destinationKey)
                .build()
        )

        Assertions.assertThat(String(response.readAllBytes(), Charsets.UTF_8)).isEqualTo(content)
    }
}
