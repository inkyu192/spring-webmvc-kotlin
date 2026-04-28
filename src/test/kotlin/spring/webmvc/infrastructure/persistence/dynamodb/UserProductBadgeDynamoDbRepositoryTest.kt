package spring.webmvc.infrastructure.persistence.dynamodb

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.testcontainers.containers.localstack.LocalStackContainer
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import spring.webmvc.domain.model.entity.UserProductBadge
import spring.webmvc.infrastructure.config.LocalStackTestContainerConfig

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserProductBadgeDynamoDbRepositoryTest {
    private lateinit var dynamoDbClient: DynamoDbClient
    private lateinit var dynamoDbEnhancedClient: DynamoDbEnhancedClient
    private lateinit var repository: UserProductBadgeDynamoDbRepository

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

        repository = UserProductBadgeDynamoDbRepository(dynamoDbEnhancedClient)
    }

    @BeforeEach
    fun setUp() {
        dynamoDbClient.createTable(
            CreateTableRequest.builder()
                .tableName(UserProductBadge.TABLE_NAME)
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
                .tableName(UserProductBadge.TABLE_NAME)
                .build()
        )
    }

    @Test
    @DisplayName("userId와 productId로 UserProductBadge를 조회한다")
    fun findByUserIdAndProductId() {
        val userId = 1L
        val productId = 10L
        val createdAt = "2025-03-01T10:30:00Z"
        val updatedAt = "2025-03-01T10:30:00Z"

        val table = dynamoDbEnhancedClient.table(
            UserProductBadge.TABLE_NAME,
            TableSchema.fromBean(UserProductBadge::class.java)
        )

        table.putItem(
            UserProductBadge(
                pk = "USER#$userId",
                sk = "PRODUCT#$productId",
                isRecommended = true,
                isPersonalPick = false,
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
        )

        val result = repository.findByUserIdAndProductId(userId, productId)

        assertThat(result).isNotNull
        assertThat(result!!.pk).isEqualTo("USER#$userId")
        assertThat(result.sk).isEqualTo("PRODUCT#$productId")
        assertThat(result.isRecommended).isTrue()
        assertThat(result.isPersonalPick).isFalse()
        assertThat(result.createdAt).isEqualTo(createdAt)
        assertThat(result.updatedAt).isEqualTo(updatedAt)
    }

    @Test
    @DisplayName("userId와 productId 목록으로 UserProductBadge를 일괄 조회한다")
    fun findByUserIdAndProductIds() {
        val userId = 1L
        val createdAt = "2025-03-01T10:30:00Z"
        val updatedAt = "2025-03-01T10:30:00Z"

        val table = dynamoDbEnhancedClient.table(
            UserProductBadge.TABLE_NAME,
            TableSchema.fromBean(UserProductBadge::class.java)
        )

        table.putItem(
            UserProductBadge(
                pk = "USER#$userId",
                sk = "PRODUCT#10",
                isRecommended = true,
                isPersonalPick = false,
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
        )
        table.putItem(
            UserProductBadge(
                pk = "USER#$userId",
                sk = "PRODUCT#20",
                isRecommended = false,
                isPersonalPick = true,
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
        )

        val results = repository.findByUserIdAndProductIds(userId, listOf(10L, 20L, 30L))

        assertThat(results).hasSize(2)
        val sortedResults = results.sortedBy { it.sk }
        assertThat(sortedResults[0].sk).isEqualTo("PRODUCT#10")
        assertThat(sortedResults[0].isRecommended).isTrue()
        assertThat(sortedResults[0].isPersonalPick).isFalse()
        assertThat(sortedResults[1].sk).isEqualTo("PRODUCT#20")
        assertThat(sortedResults[1].isRecommended).isFalse()
        assertThat(sortedResults[1].isPersonalPick).isTrue()
    }

    @Test
    @DisplayName("존재하지 않는 데이터 조회 시 null을 반환한다")
    fun findByUserIdAndProductIdNotFound() {
        val result = repository.findByUserIdAndProductId(999L, 999L)

        assertThat(result).isNull()
    }

    @Test
    @DisplayName("빈 productIds 목록으로 조회 시 빈 리스트를 반환한다")
    fun findByUserIdAndEmptyProductIds() {
        val results = repository.findByUserIdAndProductIds(1L, emptyList())

        assertThat(results).isEmpty()
    }
}
