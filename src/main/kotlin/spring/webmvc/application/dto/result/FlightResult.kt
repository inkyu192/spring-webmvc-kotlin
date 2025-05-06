package spring.webmvc.application.dto.result

import com.fasterxml.jackson.annotation.JsonCreator
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

    @JsonCreator
    constructor(
        id: Long,
        name: String,
        description: String,
        price: Int,
        quantity: Int,
        createdAt: Instant,
        flightId: Long,
        airline: String,
        flightNumber: String,
        departureAirport: String,
        arrivalAirport: String,
        departureTime: Instant,
        arrivalTime: Instant
    ) : super(
        id = id,
        category = Category.FLIGHT,
        name = name,
        description = description,
        price = price,
        quantity = quantity,
        createdAt = createdAt
    ) {
        this.flightId = flightId
        this.airline = airline
        this.flightNumber = flightNumber
        this.departureAirport = departureAirport
        this.arrivalAirport = arrivalAirport
        this.departureTime = departureTime
        this.arrivalTime = arrivalTime
    }

    constructor(flight: Flight) : super(product = flight.product) {
        this.flightId = checkNotNull(flight.id)
        this.airline = flight.airline
        this.flightNumber = flight.flightNumber
        this.departureAirport = flight.departureAirport
        this.arrivalAirport = flight.arrivalAirport
        this.departureTime = flight.departureTime
        this.arrivalTime = flight.arrivalTime
    }
}