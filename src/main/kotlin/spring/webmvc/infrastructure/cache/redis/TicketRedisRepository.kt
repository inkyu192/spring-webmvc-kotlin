package spring.webmvc.infrastructure.cache.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.cache.TicketCache
import spring.webmvc.domain.repository.cache.TicketCacheRepository
import java.time.Duration

@Repository
class TicketRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : TicketCacheRepository {
    private val logger = LoggerFactory.getLogger(TicketRedisRepository::class.java)

    companion object {
        private const val TICKET_KEY = "ticket"
    }

    override fun getTicket(productId: Long): TicketCache? {
        val key = "$TICKET_KEY:$productId"
        return runCatching {
            val value = redisTemplate.opsForValue().get(key)
            value?.let { objectMapper.readValue(it, TicketCache::class.java) }
        }.onFailure {
            logger.warn("Failed to get ticket cache for key={}: {}", key, it.message)
        }.getOrElse { null }
    }

    override fun setTicket(productId: Long, ticketCache: TicketCache) {
        val key = "$TICKET_KEY:$productId"
        runCatching {
            val valueAsString = objectMapper.writeValueAsString(ticketCache)
            val actualTimeout = Duration.ofHours(1)
            redisTemplate.opsForValue().set(key, valueAsString, actualTimeout)
        }.onFailure {
            logger.error("Failed to set ticket cache for key={}: {}", key, it.message, it)
        }
    }

    override fun deleteTicket(productId: Long): Boolean {
        val key = "$TICKET_KEY:$productId"
        return runCatching {
            redisTemplate.delete(key)
        }.onFailure {
            logger.error("Failed to delete ticket cache for key={}: {}", key, it.message, it)
        }.getOrElse { false }
    }
}