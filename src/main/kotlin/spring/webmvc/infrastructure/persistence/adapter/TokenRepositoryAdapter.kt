package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.repository.TokenRepository
import spring.webmvc.infrastructure.persistence.TokenRedisRepository

@Component
class TokenRepositoryAdapter(
    private val redisRepository: TokenRedisRepository
) : TokenRepository {
    override fun findByMemberIdOrNull(memberId: Long) = redisRepository.findByMemberIdOrNull(memberId)

    override fun save(memberId: Long, token: String) = redisRepository.save(memberId = memberId, token = token)
}