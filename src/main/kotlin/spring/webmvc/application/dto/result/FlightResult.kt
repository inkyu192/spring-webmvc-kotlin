package spring.webmvc.application.dto.result

import spring.webmvc.domain.model.cache.FlightCache
import spring.webmvc.domain.model.entity.Flight
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

class FlightResult : ProductResult {
    val flightId: Long
    val airline: String
    val flightNumber: String
    val departureAirport: String
    val arrivalAirport: String
    val departureTime: Instant
    val arrivalTime: Instant

    constructor(flight: Flight) : super(product = flight.product) {
        this.flightId = checkNotNull(flight.id)
        this.airline = flight.airline
        this.flightNumber = flight.flightNumber
        this.departureAirport = flight.departureAirport
        this.arrivalAirport = flight.arrivalAirport
        this.departureTime = flight.departureTime
        this.arrivalTime = flight.arrivalTime
    }

    constructor(flightCache: FlightCache) : super(
        id = flightCache.id,
        category = Category.FLIGHT,
        name = flightCache.name,
        description = flightCache.description,
        price = flightCache.price,
        quantity = flightCache.quantity,
        createdAt = flightCache.createdAt
    ) {
        this.flightId = flightCache.flightId
        this.airline = flightCache.airline
        this.flightNumber = flightCache.flightNumber
        this.departureAirport = flightCache.departureAirport
        this.arrivalAirport = flightCache.arrivalAirport
        this.departureTime = flightCache.departureTime
        this.arrivalTime = flightCache.arrivalTime
    }
}