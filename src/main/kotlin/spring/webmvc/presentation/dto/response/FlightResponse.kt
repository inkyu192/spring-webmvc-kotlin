package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.FlightResult
import java.time.Instant

class FlightResponse : ProductResponse {
    val flightId: Long
    val airline: String
    val flightNumber: String
    val departureAirport: String
    val arrivalAirport: String
    val departureTime: Instant
    val arrivalTime: Instant

    constructor(flightResult: FlightResult) : super(productResult = flightResult) {
        flightId = flightResult.id
        airline = flightResult.airline
        flightNumber = flightResult.flightNumber
        departureAirport = flightResult.departureAirport
        arrivalAirport = flightResult.arrivalAirport
        departureTime = flightResult.departureTime
        arrivalTime = flightResult.arrivalTime
    }
}