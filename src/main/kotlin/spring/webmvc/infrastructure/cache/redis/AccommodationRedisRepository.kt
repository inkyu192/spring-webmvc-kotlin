package spring.webmvc.infrastructure.cache.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.cache.AccommodationCache
import spring.webmvc.domain.repository.cache.AccommodationCacheRepository
import java.time.Duration

@Repository
class AccommodationRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : AccommodationCacheRepository {
    private val logger = LoggerFactory.getLogger(AccommodationRedisRepository::class.java)

    companion object {
        private const val ACCOMMODATION_KEY = "accommodation"
    }

    override fun getAccommodation(productId: Long): AccommodationCache? {
        val key = "$ACCOMMODATION_KEY:$productId"
        return runCatching {
            val value = redisTemplate.opsForValue().get(key)
            value?.let { objectMapper.readValue(it, AccommodationCache::class.java) }
        }.onFailure {
            logger.warn("Failed to get accommodation cache for key={}: {}", key, it.message)
        }.getOrElse { null }
    }

    override fun setAccommodation(productId: Long, accommodationCache: AccommodationCache) {
        val key = "$ACCOMMODATION_KEY:$productId"
        runCatching {
            val valueAsString = objectMapper.writeValueAsString(accommodationCache)
            val actualTimeout = Duration.ofHours(1)
            redisTemplate.opsForValue().set(key, valueAsString, actualTimeout)
        }.onFailure {
            logger.error("Failed to set accommodation cache for key={}: {}", key, it.message, it)
        }
    }

    override fun deleteAccommodation(productId: Long): Boolean {
        val key = "$ACCOMMODATION_KEY:$productId"
        return runCatching {
            redisTemplate.delete(key)
        }.onFailure {
            logger.error("Failed to delete accommodation cache for key={}: {}", key, it.message, it)
        }.getOrElse { false }
    }
}