package spring.webmvc.presentation.dto.response

import spring.webmvc.domain.model.entity.Flight
import java.time.Instant

data class FlightResponse(
    val id: Long,
    val name: String,
    val description: String,
    val price: Int,
    val quantity: Int,
    val createdAt: Instant,
    val airline: String,
    val flightNumber: String,
    val departureAirport: String,
    val arrivalAirport: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) {
    constructor(flight: Flight) : this(
        id = checkNotNull(flight.id),
        name = flight.product.name,
        description = flight.product.description,
        price = flight.product.price,
        quantity = flight.product.quantity,
        createdAt = flight.product.createdAt,
        airline = flight.airline,
        flightNumber = flight.flightNumber,
        departureAirport = flight.departureAirport,
        arrivalAirport = flight.arrivalAirport,
        departureTime = flight.departureTime,
        arrivalTime = flight.arrivalTime,
    )
}