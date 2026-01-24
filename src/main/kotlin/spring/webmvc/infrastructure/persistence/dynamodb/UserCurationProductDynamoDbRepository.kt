package spring.webmvc.infrastructure.persistence.dynamodb

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import spring.webmvc.domain.model.entity.UserCurationProduct
import spring.webmvc.infrastructure.exception.FailedAwsIntegrationException

@Component
class UserCurationProductDynamoDbRepository(
    dynamoDbEnhancedClient: DynamoDbEnhancedClient,
) {
    private val logger = LoggerFactory.getLogger(UserCurationProductDynamoDbRepository::class.java)

    private val table = dynamoDbEnhancedClient.table(
        UserCurationProduct.TABLE_NAME,
        TableSchema.fromBean(UserCurationProduct::class.java),
    )

    fun findByUserIdAndCurationId(userId: Long, curationId: Long): UserCurationProduct? {
        val key = Key.builder()
            .partitionValue("USER#$userId")
            .sortValue("CURATION#$curationId")
            .build()

        return runCatching {
            table.getItem(key)
        }.getOrElse { throwable ->
            logger.error("Failed to get item from DynamoDB", throwable)
            throw FailedAwsIntegrationException(serviceName = "DynamoDB", throwable = throwable)
        }
    }
}
