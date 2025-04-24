package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.time.Instant

data class FlightUpdateRequest(
    val name: String,
    val description: String,
    @field:Min(100)
    val price: Int,
    @field:Max(9999)
    val quantity: Int,
    val airline: String,
    val flightNumber: String,
    val departureAirport: String,
    val arrivalAirport: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
)