package spring.webmvc.infrastructure.persistence.dynamodb

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.*
import org.testcontainers.containers.localstack.LocalStackContainer
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import spring.webmvc.domain.model.entity.UserCurationProduct
import spring.webmvc.infrastructure.config.LocalStackTestContainerConfig

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserCurationProductDynamoDbRepositoryTest {
    private lateinit var dynamoDbClient: DynamoDbClient
    private lateinit var dynamoDbEnhancedClient: DynamoDbEnhancedClient
    private lateinit var repository: UserCurationProductDynamoDbRepository

    @BeforeAll
    fun setUpAll() {
        dynamoDbClient = DynamoDbClient.builder()
            .endpointOverride(LocalStackTestContainerConfig.localStackContainer.getEndpointOverride(LocalStackContainer.Service.DYNAMODB))
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

        dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build()

        repository = UserCurationProductDynamoDbRepository(dynamoDbEnhancedClient)
    }

    @BeforeEach
    fun setUp() {
        dynamoDbClient.createTable(
            CreateTableRequest.builder()
                .tableName(UserCurationProduct.TABLE_NAME)
                .attributeDefinitions(
                    AttributeDefinition.builder()
                        .attributeName("PK")
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                    AttributeDefinition.builder()
                        .attributeName("SK")
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName("PK")
                        .keyType(KeyType.HASH)
                        .build(),
                    KeySchemaElement.builder()
                        .attributeName("SK")
                        .keyType(KeyType.RANGE)
                        .build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build()
        )
    }

    @AfterEach
    fun tearDown() {
        dynamoDbClient.deleteTable(
            DeleteTableRequest.builder()
                .tableName(UserCurationProduct.TABLE_NAME)
                .build()
        )
    }

    @Test
    @DisplayName("userId와 curationId로 UserCurationProduct를 조회한다")
    fun findByUserIdAndCurationId() {
        val userId = 1L
        val curationId = 11L
        val productIds = listOf(1L, 3L, 11L, 13L)
        val createdAt = "2025-03-01T10:30:00Z"
        val updatedAt = "2025-03-01T10:30:00Z"

        val table = dynamoDbEnhancedClient.table(
            UserCurationProduct.TABLE_NAME,
            TableSchema.fromBean(UserCurationProduct::class.java)
        )

        table.putItem(
            UserCurationProduct(
                pk = "USER#$userId",
                sk = "CURATION#$curationId",
                productIds = productIds,
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
        )

        val result = repository.findByUserIdAndCurationId(userId, curationId)

        Assertions.assertThat(result).isNotNull
        Assertions.assertThat(result!!.pk).isEqualTo("USER#$userId")
        Assertions.assertThat(result.sk).isEqualTo("CURATION#$curationId")
        Assertions.assertThat(result.productIds).containsExactlyElementsOf(productIds)
        Assertions.assertThat(result.createdAt).isEqualTo(createdAt)
        Assertions.assertThat(result.updatedAt).isEqualTo(updatedAt)
    }

    @Test
    @DisplayName("존재하지 않는 데이터 조회 시 null을 반환한다")
    fun findByUserIdAndCurationIdNotFound() {
        val result = repository.findByUserIdAndCurationId(999L, 999L)

        Assertions.assertThat(result).isNull()
    }
}
