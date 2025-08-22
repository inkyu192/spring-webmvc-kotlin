package spring.webmvc.domain.repository.cache

import spring.webmvc.application.dto.result.FlightResult
import java.time.Duration

interface FlightCacheRepository {
    fun getFlight(productId: Long): FlightResult?
    fun setFlight(productId: Long, flightResult: FlightResult, timeout: Duration? = null)
    fun deleteFlight(productId: Long): Boolean
}