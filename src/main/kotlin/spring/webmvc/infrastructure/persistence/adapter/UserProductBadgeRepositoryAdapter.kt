package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.repository.UserProductBadgeRepository
import spring.webmvc.infrastructure.persistence.dynamodb.UserProductBadgeDynamoDbRepository

@Component
class UserProductBadgeRepositoryAdapter(
    private val dynamoDbRepository: UserProductBadgeDynamoDbRepository,
) : UserProductBadgeRepository {
    override fun findByUserIdAndProductId(userId: Long, productId: Long) =
        dynamoDbRepository.findByUserIdAndProductId(userId = userId, productId = productId)

    override fun findByUserIdAndProductIds(userId: Long, productIds: List<Long>) =
        dynamoDbRepository.findByUserIdAndProductIds(userId = userId, productIds = productIds)
}
