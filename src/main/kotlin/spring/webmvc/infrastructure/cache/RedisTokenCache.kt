package spring.webmvc.infrastructure.cache

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import spring.webmvc.domain.cache.TokenCache
import java.time.Duration

@Component
class RedisTokenCache(
    private val redisTemplate: RedisTemplate<String, String>,
) : TokenCache {
    private fun createKey(memberId: Long) = "member:$memberId:token:refresh"

    override fun get(memberId: Long) = createKey(memberId).let { redisTemplate.opsForValue().get(it) }

    override fun set(memberId: Long, value: String) {
        redisTemplate.opsForValue().set(createKey(memberId = memberId), value, Duration.ofDays(7))
    }
}