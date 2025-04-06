package spring.webmvc.infrastructure.persistence

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class RequestLockRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>
) {
    private fun createKey(memberId: Long, method: String, uri: String) = "request-lock:$memberId:$method:$uri"

    fun setIfAbsent(memberId: Long, method: String, uri: String) =
        createKey(memberId, method, uri).let {
            redisTemplate.opsForValue().setIfAbsent(it, "1", Duration.ofSeconds(1)) == true
        }
}