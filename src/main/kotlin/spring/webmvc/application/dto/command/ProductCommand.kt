package spring.webmvc.application.dto.command

import spring.webmvc.domain.model.enums.Category
import java.time.Instant

data class ProductPutCommand(
    val id: Long? = null,
    val category: Category,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val detail: ProductDetailPutCommand,
)

data class ProductDeleteCommand(
    val id: Long,
    val category: Category,
)

sealed interface ProductDetailPutCommand

data class TransportPutCommand(
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductDetailPutCommand

data class AccommodationPutCommand(
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
) : ProductDetailPutCommand