package spring.webmvc.infrastructure.cache.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.cache.FlightCache
import spring.webmvc.domain.repository.cache.FlightCacheRepository
import java.time.Duration

@Repository
class FlightRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : FlightCacheRepository {
    private val logger = LoggerFactory.getLogger(FlightRedisRepository::class.java)

    companion object {
        private const val FLIGHT_KEY = "flight"
    }

    override fun getFlight(productId: Long): FlightCache? {
        val key = "$FLIGHT_KEY:$productId"
        return runCatching {
            val value = redisTemplate.opsForValue().get(key)
            value?.let { objectMapper.readValue(it, FlightCache::class.java) }
        }.onFailure {
            logger.warn("Failed to get flight cache for key={}: {}", key, it.message)
        }.getOrElse { null }
    }

    override fun setFlight(productId: Long, flightCache: FlightCache) {
        val key = "$FLIGHT_KEY:$productId"
        runCatching {
            val valueAsString = objectMapper.writeValueAsString(flightCache)
            val actualTimeout = Duration.ofHours(1)
            redisTemplate.opsForValue().set(key, valueAsString, actualTimeout)
        }.onFailure {
            logger.error("Failed to set flight cache for key={}: {}", key, it.message, it)
        }
    }

    override fun deleteFlight(productId: Long): Boolean {
        val key = "$FLIGHT_KEY:$productId"
        return runCatching {
            redisTemplate.delete(key)
        }.onFailure {
            logger.error("Failed to delete flight cache for key={}: {}", key, it.message, it)
        }.getOrElse { false }
    }
}