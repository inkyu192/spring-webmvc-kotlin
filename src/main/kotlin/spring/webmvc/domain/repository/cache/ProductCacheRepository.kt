package spring.webmvc.domain.repository.cache

import java.time.Duration

interface ProductCacheRepository {
    fun getProductStock(productId: Long): Long?
    fun setProductStockIfAbsent(productId: Long, stock: Long, timeout: Duration? = null): Boolean
    fun deleteProductStock(productId: Long): Boolean
    fun incrementProductViewCount(productId: Long, delta: Long): Long?
    fun incrementProductStock(productId: Long, delta: Long): Long?
    fun decrementProductStock(productId: Long, delta: Long): Long?
}