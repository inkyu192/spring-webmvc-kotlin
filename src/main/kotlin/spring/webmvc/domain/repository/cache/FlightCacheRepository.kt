package spring.webmvc.domain.repository.cache

import spring.webmvc.domain.model.cache.FlightCache

interface FlightCacheRepository {
    fun getFlight(productId: Long): FlightCache?
    fun setFlight(productId: Long, flightCache: FlightCache)
    fun deleteFlight(productId: Long): Boolean
}