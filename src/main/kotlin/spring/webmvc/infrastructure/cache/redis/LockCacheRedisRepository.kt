package spring.webmvc.infrastructure.cache.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import spring.webmvc.domain.repository.LockCacheRepository
import java.time.Duration

@Component
class LockCacheRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) : LockCacheRepository {
    override fun tryLock(method: String, uri: String, hash: String) =
        redisTemplate.opsForValue()
            .setIfAbsent(
                "request-lock:$method:$uri:$hash",
                "1",
                Duration.ofSeconds(1)
            ) ?: false
}