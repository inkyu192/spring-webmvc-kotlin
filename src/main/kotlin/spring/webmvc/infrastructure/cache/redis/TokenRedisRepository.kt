package spring.webmvc.infrastructure.cache.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import spring.webmvc.domain.repository.cache.TokenCacheRepository

@Repository
class TokenRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) : TokenCacheRepository {

    override fun setRefreshToken(memberId: Long, refreshToken: String) {
        redisTemplate.opsForValue().set("member:$memberId:token:refresh", refreshToken)
    }

    override fun getRefreshToken(memberId: Long) = redisTemplate.opsForValue().get("member:$memberId:token:refresh")
}