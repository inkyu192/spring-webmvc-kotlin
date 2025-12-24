package spring.webmvc.domain.repository.cache

import spring.webmvc.domain.model.cache.TransportCache

interface TransportCacheRepository {
    fun getTransport(productId: Long): TransportCache?
    fun setTransport(productId: Long, transportCache: TransportCache)
    fun deleteTransport(productId: Long): Boolean
}
