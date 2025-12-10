package spring.webmvc.infrastructure.cache.redis

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import spring.webmvc.domain.repository.cache.TokenCacheRepository

@Repository
class TokenRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) : TokenCacheRepository {
    private val logger = LoggerFactory.getLogger(TokenRedisRepository::class.java)

    companion object {
        private const val REFRESH_TOKEN_KEY = "user:%d:token:refresh"
    }

    override fun setRefreshToken(userId: Long, refreshToken: String) {
        val key = REFRESH_TOKEN_KEY.format(userId)
        runCatching {
            redisTemplate.opsForValue().set(key, refreshToken)
        }.onFailure {
            logger.error("Failed to set refresh token for userId={}: {}", userId, it.message, it)
        }
    }

    override fun getRefreshToken(userId: Long): String? {
        val key = REFRESH_TOKEN_KEY.format(userId)
        return runCatching {
            redisTemplate.opsForValue().get(key)
        }.onFailure {
            logger.warn("Failed to get refresh token for userId={}: {}", userId, it.message)
        }.getOrElse { null }
    }
}