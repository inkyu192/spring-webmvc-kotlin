package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Flight

interface FlightRepository {
    fun findByIdOrNull(id: Long): Flight?
    fun findByProductId(productId: Long): Flight?
    fun save(flight: Flight): Flight
    fun delete(flight: Flight)
}