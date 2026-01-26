package spring.webmvc.infrastructure.cache.redis

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import spring.webmvc.domain.repository.cache.ProductCacheRepository
import java.time.Duration

@Repository
class ProductRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) : ProductCacheRepository {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val PRODUCT_STOCK_KEY = "product:%d:stock"
        private const val PRODUCT_VIEW_COUNT_KEY = "product:%d:view-count"
    }

    override fun getProductStock(productId: Long): Long? {
        val key = PRODUCT_STOCK_KEY.format(productId)

        return runCatching {
            redisTemplate.opsForValue().get(key)?.toLong()
        }.onFailure {
            logger.warn("Failed to get product stock for productId={}: {}", productId, it.message)
        }.getOrElse { null }
    }

    override fun setProductStockIfAbsent(productId: Long, stock: Long, timeout: Duration?): Boolean {
        val key = PRODUCT_STOCK_KEY.format(productId)

        return runCatching {
            if (timeout != null) {
                redisTemplate.opsForValue().setIfAbsent(key, stock.toString(), timeout) ?: false
            } else {
                redisTemplate.opsForValue().setIfAbsent(key, stock.toString()) ?: false
            }
        }.onFailure {
            logger.error("Failed to set product stock if absent for productId={}: {}", productId, it.message, it)
        }.getOrElse { false }
    }

    override fun incrementProductViewCount(productId: Long, delta: Long): Long? {
        val key = PRODUCT_VIEW_COUNT_KEY.format(productId)
        return runCatching {
            redisTemplate.opsForValue().increment(key, delta)
        }.onFailure {
            logger.error("Failed to increment product view count for productId={}: {}", productId, it.message, it)
        }.getOrElse { null }
    }

    override fun incrementProductStock(productId: Long, delta: Long): Long? {
        val key = PRODUCT_STOCK_KEY.format(productId)
        return runCatching {
            redisTemplate.opsForValue().increment(key, delta)
        }.onFailure {
            logger.error("Failed to increment product stock for productId={}: {}", productId, it.message, it)
        }.getOrElse { null }
    }

    override fun decrementProductStock(productId: Long, delta: Long): Long? {
        val key = PRODUCT_STOCK_KEY.format(productId)
        return runCatching {
            redisTemplate.opsForValue().decrement(key, delta)
        }.onFailure {
            logger.error("Failed to decrement product stock for productId={}: {}", productId, it.message, it)
        }.getOrElse { null }
    }
}
