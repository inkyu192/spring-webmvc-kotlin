package spring.webmvc.infrastructure.cache.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.cache.TransportCache
import spring.webmvc.domain.repository.cache.TransportCacheRepository
import java.time.Duration

@Repository
class TransportRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : TransportCacheRepository {
    private val logger = LoggerFactory.getLogger(TransportRedisRepository::class.java)

    companion object {
        private const val TRANSPORT_KEY = "transport"
    }

    override fun getTransport(productId: Long): TransportCache? {
        val key = "$TRANSPORT_KEY:$productId"
        return runCatching {
            val value = redisTemplate.opsForValue().get(key)
            value?.let { objectMapper.readValue(it, TransportCache::class.java) }
        }.onFailure {
            logger.warn("Failed to get transport cache for key={}: {}", key, it.message)
        }.getOrElse { null }
    }

    override fun setTransport(productId: Long, transportCache: TransportCache) {
        val key = "$TRANSPORT_KEY:$productId"
        runCatching {
            val valueAsString = objectMapper.writeValueAsString(transportCache)
            val actualTimeout = Duration.ofHours(1)
            redisTemplate.opsForValue().set(key, valueAsString, actualTimeout)
        }.onFailure {
            logger.error("Failed to set transport cache for key={}: {}", key, it.message, it)
        }
    }

    override fun deleteTransport(productId: Long): Boolean {
        val key = "$TRANSPORT_KEY:$productId"
        return runCatching {
            redisTemplate.delete(key)
        }.onFailure {
            logger.error("Failed to delete transport cache for key={}: {}", key, it.message, it)
        }.getOrElse { false }
    }
}
