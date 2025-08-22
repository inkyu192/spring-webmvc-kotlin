package spring.webmvc.infrastructure.cache.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import spring.webmvc.domain.repository.cache.RequestLockCacheRepository
import java.time.Duration

@Repository
class RequestLockRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) : RequestLockCacheRepository {
    override fun tryLock(method: String, uri: String, hash: String) =
        redisTemplate.opsForValue()
            .setIfAbsent(
                "request-lock:$method:$uri:$hash",
                "1",
                Duration.ofSeconds(1)
            ) ?: false
}