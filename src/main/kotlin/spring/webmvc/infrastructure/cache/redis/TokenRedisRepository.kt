package spring.webmvc.infrastructure.cache.redis

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import spring.webmvc.domain.repository.cache.TokenCacheRepository
import spring.webmvc.infrastructure.properties.AppProperties
import java.util.concurrent.TimeUnit

@Repository
class TokenRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val appProperties: AppProperties,
) : TokenCacheRepository {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val REFRESH_TOKEN_KEY = "user:%d:device:%s:refresh-token"
    }

    override fun setRefreshToken(userId: Long, deviceId: String, refreshToken: String) {
        val key = REFRESH_TOKEN_KEY.format(userId, deviceId)

        try {
            val ttl = appProperties.jwt.refreshToken.expiration.seconds
            redisTemplate.opsForValue().set(key, refreshToken, ttl, TimeUnit.SECONDS)
        } catch (e: Exception) {
            logger.error("Failed to set refresh token for userId={}, deviceId={}: {}", userId, deviceId, e.message, e)
        }
    }

    override fun getRefreshToken(userId: Long, deviceId: String): String? {
        val key = REFRESH_TOKEN_KEY.format(userId, deviceId)

        return try {
            redisTemplate.opsForValue().get(key)
        } catch (e: Exception) {
            logger.warn("Failed to get refresh token for userId={}, deviceId={}: {}", userId, deviceId, e.message)
            null
        }
    }

    override fun removeRefreshToken(userId: Long, deviceId: String) {
        val key = REFRESH_TOKEN_KEY.format(userId, deviceId)

        try {
            redisTemplate.delete(key)
        } catch (e: Exception) {
            logger.error(
                "Failed to remove refresh token for userId={}, deviceId={}: {}",
                userId,
                deviceId,
                e.message,
                e
            )
        }
    }
}
