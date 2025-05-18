package spring.webmvc.presentation.dto.request

import spring.webmvc.domain.model.enums.Category
import java.time.Instant

class FlightCreateRequest(
    category: Category,
    name: String,
    description: String,
    price: Long,
    quantity: Long,
    val airline: String,
    val flightNumber: String,
    val departureAirport: String,
    val arrivalAirport: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductCreateRequest(category, name, description, price, quantity)