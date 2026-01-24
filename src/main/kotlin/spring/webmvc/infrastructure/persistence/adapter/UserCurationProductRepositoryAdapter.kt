package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.repository.UserCurationProductRepository
import spring.webmvc.infrastructure.persistence.dynamodb.UserCurationProductDynamoDbRepository

@Component
class UserCurationProductRepositoryAdapter(
    private val dynamoDbRepository: UserCurationProductDynamoDbRepository,
) : UserCurationProductRepository {
    override fun findByUserIdAndCurationId(userId: Long, curationId: Long) =
        dynamoDbRepository.findByUserIdAndCurationId(userId = userId, curationId = curationId)
}
