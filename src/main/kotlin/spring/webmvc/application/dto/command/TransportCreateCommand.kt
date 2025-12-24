package spring.webmvc.application.dto.command

import spring.webmvc.domain.model.enums.Category
import java.time.Instant

class TransportCreateCommand(
    category: Category,
    name: String,
    description: String,
    price: Long,
    quantity: Long,
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductCreateCommand(
    category = category,
    name = name,
    description = description,
    price = price,
    quantity = quantity,
)
