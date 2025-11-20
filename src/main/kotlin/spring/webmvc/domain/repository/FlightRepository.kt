package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Flight

interface FlightRepository {
    fun findById(id: Long): Flight
    fun save(flight: Flight): Flight
    fun delete(flight: Flight)
}