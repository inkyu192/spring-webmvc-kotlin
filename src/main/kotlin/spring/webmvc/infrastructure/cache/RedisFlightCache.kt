package spring.webmvc.infrastructure.cache

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import spring.webmvc.domain.cache.FlightCache
import java.time.Duration

@Component
class RedisFlightCache(
    private val redisTemplate: RedisTemplate<String, String>,
) : FlightCache {
    private fun createKey(productId: Long) = "product:$productId"

    override fun get(id: Long) = runCatching { redisTemplate.opsForValue().get(createKey(productId = id)) }.getOrNull()

    override fun set(id: Long, value: String) {
        redisTemplate.opsForValue().set(createKey(productId = id), value, Duration.ofHours(1))
    }
}