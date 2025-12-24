package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.TransportResult
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

class TransportResponse(
    id: Long,
    category: Category,
    name: String,
    description: String,
    price: Long,
    quantity: Long,
    createdAt: Instant,
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductResponse(
    id = id,
    category = category,
    name = name,
    description = description,
    price = price,
    quantity = quantity,
    createdAt = createdAt,
) {
    companion object {
        fun from(result: TransportResult) =
            TransportResponse(
                id = result.id,
                category = result.category,
                name = result.name,
                description = result.description,
                price = result.price,
                quantity = result.quantity,
                createdAt = result.createdAt,
                departureLocation = result.departureLocation,
                arrivalLocation = result.arrivalLocation,
                departureTime = result.departureTime,
                arrivalTime = result.arrivalTime,
            )
    }
}
