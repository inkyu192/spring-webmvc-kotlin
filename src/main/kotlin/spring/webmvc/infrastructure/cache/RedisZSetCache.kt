package spring.webmvc.infrastructure.cache

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import spring.webmvc.domain.cache.ZSetCache
import java.time.Duration

@Component
class RedisZSetCache(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : ZSetCache {
    private val logger = LoggerFactory.getLogger(RedisZSetCache::class.java)

    override fun <T> add(key: String, value: T, score: Double) {
        serialize(key = key, value = value)?.let {
            redisTemplate.opsForZSet().add(key, it, score)
        }
    }

    override fun expire(key: String, timeout: Duration) = redisTemplate.expire(key, timeout) ?: false

    override fun size(key: String) = redisTemplate.opsForZSet().size(key) ?: 0L

    override fun <T> range(key: String, start: Long, end: Long, clazz: Class<T>) =
        redisTemplate.opsForZSet().range(key, start, end)
            ?.mapNotNull { deserialize(key = key, value = it, clazz = clazz) }
            ?.toCollection(LinkedHashSet())
            ?: emptySet()

    private fun <T> serialize(key: String, value: T): String? {
        if (value is String) {
            return value
        }

        return runCatching { objectMapper.writeValueAsString(value) }
            .onFailure { logger.warn("Failed to serialize zSet for key={}, value={}: {}", key, value, it.message) }
            .getOrNull()
    }

    private fun <T> deserialize(key: String, value: String, clazz: Class<T>): T? {
        if (clazz == String::class.java) {
            return clazz.cast(value) as T
        }

        return runCatching { objectMapper.readValue(value, clazz) }
            .onFailure { logger.warn("Failed to deserialize zSet for key={}, value={}: {}", key, value, it.message) }
            .getOrNull()
    }
}