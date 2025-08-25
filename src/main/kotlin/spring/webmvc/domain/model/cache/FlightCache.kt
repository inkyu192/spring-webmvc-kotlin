package spring.webmvc.domain.model.cache

import java.time.Instant

data class FlightCache(
    val id: Long,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val createdAt: Instant,
    val flightId: Long,
    val airline: String,
    val flightNumber: String,
    val departureAirport: String,
    val arrivalAirport: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) {
    companion object {
        fun create(
            id: Long,
            name: String,
            description: String,
            price: Long,
            quantity: Long,
            createdAt: Instant,
            flightId: Long,
            airline: String,
            flightNumber: String,
            departureAirport: String,
            arrivalAirport: String,
            departureTime: Instant,
            arrivalTime: Instant,
        ) = FlightCache(
            id = id,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            createdAt = createdAt,
            flightId = flightId,
            airline = airline,
            flightNumber = flightNumber,
            departureAirport = departureAirport,
            arrivalAirport = arrivalAirport,
            departureTime = departureTime,
            arrivalTime = arrivalTime,
        )
    }
}