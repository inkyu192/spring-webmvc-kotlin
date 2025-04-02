package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Token
import spring.webmvc.domain.repository.TokenRepository
import spring.webmvc.infrastructure.persistence.TokenRedisRepository

@Component
class TokenRepositoryAdapter(
    private val redisRepository: TokenRedisRepository
) : TokenRepository {
    override fun findByIdOrNull(id: Long) = redisRepository.findByIdOrNull(id)

    override fun save(token: Token) = redisRepository.save(token)
}