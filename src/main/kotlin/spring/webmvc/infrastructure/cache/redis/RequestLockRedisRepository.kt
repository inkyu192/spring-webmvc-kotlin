package spring.webmvc.infrastructure.cache.redis

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import spring.webmvc.domain.repository.cache.RequestLockCacheRepository
import java.time.Duration

@Repository
class RequestLockRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) : RequestLockCacheRepository {
    private val logger = LoggerFactory.getLogger(RequestLockRedisRepository::class.java)

    companion object {
        private const val REQUEST_LOCK_KEY = "request-lock:%s:%s:%s"
        private val LOCK_TIMEOUT = Duration.ofSeconds(1)
    }

    override fun tryLock(method: String, uri: String, hash: String): Boolean {
        val key = REQUEST_LOCK_KEY.format(method, uri, hash)
        return runCatching {
            redisTemplate.opsForValue().setIfAbsent(key, "1", LOCK_TIMEOUT) ?: false
        }.onFailure {
            logger.error("Failed to acquire request lock for method={}, uri={}, hash={}: {}", method, uri, hash, it.message, it)
        }.getOrElse { false }
    }
}