package spring.webmvc.infrastructure.persistence

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class TokenRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>
) {
    private fun createKey(memberId: Long) = "member:$memberId:token:refresh"

    fun findByMemberIdOrNull(memberId: Long) = createKey(memberId).let { redisTemplate.opsForValue().get(it) }

    fun save(memberId: Long, token: String) =
        createKey(memberId)
            .also { redisTemplate.opsForValue().set(it, token, Duration.ofDays(7)) }
            .let { token }
}