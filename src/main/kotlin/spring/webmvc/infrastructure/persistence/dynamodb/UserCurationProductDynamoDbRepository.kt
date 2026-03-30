package spring.webmvc.infrastructure.persistence.dynamodb

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException
import spring.webmvc.domain.model.entity.UserCurationProduct
import spring.webmvc.infrastructure.exception.FailedAwsException

@Component
class UserCurationProductDynamoDbRepository(
    dynamoDbEnhancedClient: DynamoDbEnhancedClient,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val table = dynamoDbEnhancedClient.table(
        UserCurationProduct.TABLE_NAME,
        TableSchema.fromBean(UserCurationProduct::class.java),
    )

    fun findByUserIdAndCurationId(userId: Long, curationId: Long): UserCurationProduct? {
        val key = Key.builder()
            .partitionValue("USER#$userId")
            .sortValue("CURATION#$curationId")
            .build()

        return try {
            table.getItem(key)
        } catch (e: DynamoDbException) {
            logger.error("Failed to get item from DynamoDB", e)
            throw FailedAwsException(serviceName = e.awsErrorDetails().serviceName(), throwable = e)
        }
    }
}
