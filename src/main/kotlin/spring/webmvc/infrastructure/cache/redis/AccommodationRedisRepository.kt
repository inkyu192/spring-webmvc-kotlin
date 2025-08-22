package spring.webmvc.infrastructure.cache.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import spring.webmvc.application.dto.result.AccommodationResult

import spring.webmvc.domain.repository.cache.AccommodationCacheRepository
import java.time.Duration

@Repository
class AccommodationRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : AccommodationCacheRepository {
    private val logger = LoggerFactory.getLogger(AccommodationRedisRepository::class.java)
    
    companion object {
        private const val KEY_PREFIX = "accommodation:%d"
        private val DEFAULT_TIMEOUT = Duration.ofHours(1)
    }

    override fun getAccommodation(productId: Long): AccommodationResult? {
        return try {
            val key = KEY_PREFIX.format(productId)
            val value = redisTemplate.opsForValue().get(key)
            value?.let { objectMapper.readValue(it, AccommodationResult::class.java) }
        } catch (e: Exception) {
            logger.error("Redis get 연산 중 오류 발생. productId: $productId", e)
            null
        }
    }

    override fun setAccommodation(productId: Long, accommodationResult: AccommodationResult, timeout: Duration?) {
        try {
            val key = KEY_PREFIX.format(productId)
            val valueAsString = objectMapper.writeValueAsString(accommodationResult)
            val actualTimeout = timeout ?: DEFAULT_TIMEOUT
            redisTemplate.opsForValue().set(key, valueAsString, actualTimeout)
        } catch (e: Exception) {
            logger.error("Redis set 연산 중 오류 발생. productId: $productId", e)
        }
    }

    override fun deleteAccommodation(productId: Long): Boolean {
        return try {
            val key = KEY_PREFIX.format(productId)
            redisTemplate.delete(key)
        } catch (e: Exception) {
            logger.error("Redis delete 연산 중 오류 발생. productId: $productId", e)
            false
        }
    }
}