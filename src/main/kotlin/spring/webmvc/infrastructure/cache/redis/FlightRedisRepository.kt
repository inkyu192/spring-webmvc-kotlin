package spring.webmvc.infrastructure.cache.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import spring.webmvc.application.dto.result.FlightResult

import spring.webmvc.domain.repository.cache.FlightCacheRepository
import java.time.Duration

@Repository
class FlightRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : FlightCacheRepository {
    private val logger = LoggerFactory.getLogger(FlightRedisRepository::class.java)
    
    companion object {
        private const val KEY_PREFIX = "flight:%d"
        private val DEFAULT_TIMEOUT = Duration.ofHours(1)
    }

    override fun getFlight(productId: Long): FlightResult? {
        return try {
            val key = KEY_PREFIX.format(productId)
            val value = redisTemplate.opsForValue().get(key)
            value?.let { objectMapper.readValue(it, FlightResult::class.java) }
        } catch (e: Exception) {
            logger.error("Redis get 연산 중 오류 발생. productId: $productId", e)
            null
        }
    }

    override fun setFlight(productId: Long, flightResult: FlightResult, timeout: Duration?) {
        try {
            val key = KEY_PREFIX.format(productId)
            val valueAsString = objectMapper.writeValueAsString(flightResult)
            val actualTimeout = timeout ?: DEFAULT_TIMEOUT
            redisTemplate.opsForValue().set(key, valueAsString, actualTimeout)
        } catch (e: Exception) {
            logger.error("Redis set 연산 중 오류 발생. productId: $productId", e)
        }
    }

    override fun deleteFlight(productId: Long): Boolean {
        return try {
            val key = KEY_PREFIX.format(productId)
            redisTemplate.delete(key)
        } catch (e: Exception) {
            logger.error("Redis delete 연산 중 오류 발생. productId: $productId", e)
            false
        }
    }
}