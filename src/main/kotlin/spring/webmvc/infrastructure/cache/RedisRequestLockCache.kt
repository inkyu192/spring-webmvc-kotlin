package spring.webmvc.infrastructure.cache

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import spring.webmvc.domain.cache.RequestLockCache
import java.time.Duration

@Component
class RedisRequestLockCache(
    private val redisTemplate: RedisTemplate<String, String>,
) : RequestLockCache {
    private fun createKey(memberId: Long, method: String, uri: String) = "request-lock:$memberId:$method:$uri"

    override fun setIfAbsent(memberId: Long, method: String, uri: String) =
        createKey(memberId = memberId, method = method, uri = uri).let {
            redisTemplate.opsForValue().setIfAbsent(it, "1", Duration.ofSeconds(1)) == true
        }
}