package spring.webmvc.infrastructure.cache

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import spring.webmvc.domain.cache.KeyValueCache
import java.time.Duration

@Component
class RedisKeyValueCache(
    private val redisTemplate: RedisTemplate<String, String>,
) : KeyValueCache {
    override fun get(key: String) = redisTemplate.opsForValue().get(key)

    override fun set(key: String, value: String, timeout: Duration?) {
        if (timeout == null) {
            redisTemplate.opsForValue().set(key, value)
        } else {
            redisTemplate.opsForValue().set(key, value, timeout)
        }
    }

    override fun setIfAbsent(key: String, value: String, timeout: Duration?): Boolean {
        val result = if (timeout == null) {
            redisTemplate.opsForValue().setIfAbsent(key, value)
        } else {
            redisTemplate.opsForValue().setIfAbsent(key, value, timeout)
        }

        return result == true
    }
}