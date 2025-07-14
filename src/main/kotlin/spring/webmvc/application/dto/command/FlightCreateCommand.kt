package spring.webmvc.application.dto.command

import spring.webmvc.domain.model.enums.Category
import java.time.Instant

class FlightCreateCommand(
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
) : ProductCreateCommand(
    category = category,
    name = name,
    description = description,
    price = price,
    quantity = quantity
)