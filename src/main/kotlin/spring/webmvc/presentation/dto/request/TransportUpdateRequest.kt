package spring.webmvc.presentation.dto.request

import spring.webmvc.application.dto.command.TransportUpdateCommand
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

class TransportUpdateRequest(
    category: Category,
    name: String,
    description: String,
    price: Long,
    quantity: Long,
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductUpdateRequest(
    category = category,
    name = name,
    description = description,
    price = price,
    quantity = quantity,
) {
    override fun toCommand() =
        TransportUpdateCommand(
            category = category,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            departureLocation = departureLocation,
            arrivalLocation = arrivalLocation,
            departureTime = departureTime,
            arrivalTime = arrivalTime,
        )
}
