package spring.webmvc.infrastructure.persistence.dynamodb

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.ReadBatch
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException
import spring.webmvc.domain.model.entity.UserProductBadge
import spring.webmvc.infrastructure.exception.FailedAwsException

@Component
class UserProductBadgeDynamoDbRepository(
    private val dynamoDbEnhancedClient: DynamoDbEnhancedClient,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val table = dynamoDbEnhancedClient.table(
        UserProductBadge.TABLE_NAME,
        TableSchema.fromBean(UserProductBadge::class.java),
    )

    fun findByUserIdAndProductId(userId: Long, productId: Long): UserProductBadge? {
        val key = Key.builder()
            .partitionValue("USER#$userId")
            .sortValue("PRODUCT#$productId")
            .build()

        return try {
            table.getItem(key)
        } catch (e: DynamoDbException) {
            logger.error("Failed to get item from DynamoDB", e)
            throw FailedAwsException(serviceName = e.awsErrorDetails().serviceName(), throwable = e)
        }
    }

    fun findByUserIdAndProductIds(userId: Long, productIds: List<Long>): List<UserProductBadge> {
        if (productIds.isEmpty()) return emptyList()

        val readBatch = ReadBatch.builder(UserProductBadge::class.java)
            .mappedTableResource(table)
            .apply {
                productIds.forEach { productId ->
                    addGetItem(
                        Key.builder()
                            .partitionValue("USER#$userId")
                            .sortValue("PRODUCT#$productId")
                            .build()
                    )
                }
            }
            .build()

        return try {
            val results = dynamoDbEnhancedClient.batchGetItem { it.readBatches(readBatch) }
            results.resultsForTable(table).toList()
        } catch (e: DynamoDbException) {
            logger.error("Failed to batch get items from DynamoDB", e)
            throw FailedAwsException(serviceName = e.awsErrorDetails().serviceName(), throwable = e)
        }
    }
}
