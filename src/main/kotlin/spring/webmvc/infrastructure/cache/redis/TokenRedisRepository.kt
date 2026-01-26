package spring.webmvc.infrastructure.cache.redis

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import spring.webmvc.domain.repository.cache.TokenCacheRepository
import spring.webmvc.infrastructure.properties.JwtProperties
import java.util.concurrent.TimeUnit

@Repository
class TokenRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val jwtProperties: JwtProperties,
) : TokenCacheRepository {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val REFRESH_TOKEN_KEY = "user:%d:refresh-tokens"
        private const val MAX_TOKENS = 3
    }

    override fun addRefreshToken(userId: Long, refreshToken: String) {
        val key = REFRESH_TOKEN_KEY.format(userId)

        runCatching {
            val score = System.currentTimeMillis().toDouble()
            redisTemplate.opsForZSet().add(key, refreshToken, score)

            val size = redisTemplate.opsForZSet().zCard(key) ?: 0
            if (size > MAX_TOKENS) {
                redisTemplate.opsForZSet().removeRange(key, 0, size - MAX_TOKENS - 1)
            }

            val ttl = jwtProperties.refreshToken.expiration.seconds
            redisTemplate.expire(key, ttl, TimeUnit.SECONDS)
        }.onFailure {
            logger.error("Failed to add refresh token for userId={}: {}", userId, it.message, it)
        }
    }

    override fun getRefreshToken(userId: Long, refreshToken: String): String? {
        val key = REFRESH_TOKEN_KEY.format(userId)

        return runCatching {
            if (redisTemplate.opsForZSet().score(key, refreshToken) != null) refreshToken else null
        }.onFailure {
            logger.warn("Failed to get refresh token for userId={}: {}", userId, it.message)
        }.getOrNull()
    }

    override fun removeRefreshToken(userId: Long, refreshToken: String) {
        val key = REFRESH_TOKEN_KEY.format(userId)

        runCatching {
            redisTemplate.opsForZSet().remove(key, refreshToken)
        }.onFailure {
            logger.error("Failed to remove refresh token for userId={}: {}", userId, it.message, it)
        }
    }
}
