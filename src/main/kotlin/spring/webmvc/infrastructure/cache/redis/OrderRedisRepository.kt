package spring.webmvc.infrastructure.cache.redis

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import spring.webmvc.domain.repository.cache.OrderCacheRepository
import java.time.Duration

@Repository
class OrderRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) : OrderCacheRepository {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val ORDER_SEQUENCE_KEY = "order:sequence:%s"
    }

    override fun incrementSequence(date: String): Long? {
        val key = ORDER_SEQUENCE_KEY.format(date)
        return try {
            val seq = redisTemplate.opsForValue().increment(key)
            if (seq == 1L) {
                redisTemplate.expire(key, Duration.ofDays(1))
            }
            seq
        } catch (e: Exception) {
            logger.warn("Redis increment failed: {}", e.message)
            null
        }
    }

    override fun setSequence(date: String, value: Long): Boolean {
        val key = ORDER_SEQUENCE_KEY.format(date)
        return try {
            redisTemplate.opsForValue().set(key, value.toString())
            redisTemplate.expire(key, Duration.ofDays(1))
            true
        } catch (e: Exception) {
            logger.warn("Redis set failed: {}", e.message)
            false
        }
    }
}
