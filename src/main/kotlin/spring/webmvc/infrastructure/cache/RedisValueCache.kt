package spring.webmvc.infrastructure.cache

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import spring.webmvc.domain.cache.ValueCache
import java.time.Duration

@Component
class RedisValueCache(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : ValueCache {
    private val logger = LoggerFactory.getLogger(RedisValueCache::class.java)

    override fun get(key: String) = redisTemplate.opsForValue().get(key)

    override fun <T> get(key: String, clazz: Class<T>): T? {
        val value = redisTemplate.opsForValue().get(key)
        return if (value != null) deserialize(key = key, value = value, clazz = clazz) else null
    }

    override fun <T> set(key: String, value: T, timeout: Duration?) {
        serialize(key = key, value = value)?.let {
            if (timeout == null) {
                redisTemplate.opsForValue().set(key, it)
            } else {
                redisTemplate.opsForValue().set(key, it, timeout)
            }
        }
    }

    override fun setIfAbsent(key: String, value: String, timeout: Duration?): Boolean {
        val result = if (timeout == null) {
            redisTemplate.opsForValue().setIfAbsent(key, value)
        } else {
            redisTemplate.opsForValue().setIfAbsent(key, value, timeout)
        }

        return result ?: false
    }

    override fun delete(key: String) = redisTemplate.delete(key)

    override fun increment(key: String, delta: Long) = redisTemplate.opsForValue().increment(key, delta)

    override fun decrement(key: String, delta: Long) = redisTemplate.opsForValue().decrement(key, delta)

    private fun <T> serialize(key: String, value: T): String? {
        if (value is String) {
            return value
        }

        return runCatching { objectMapper.writeValueAsString(value) }
            .onFailure { logger.warn("Failed to serialize value for key={}, value={}: {}", key, value, it.message) }
            .getOrNull()
    }

    private fun <T> deserialize(key: String, value: String, clazz: Class<T>): T? {
        if (clazz == String::class.java) {
            return clazz.cast(value) as T
        }

        return runCatching { objectMapper.readValue(value, clazz) }
            .onFailure { logger.warn("Failed to deserialize value for key={}, value={}: {}", key, value, it.message) }
            .getOrNull()
    }
}