package spring.webmvc.application.dto.command

import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.model.enums.ProductStatus
import java.time.Instant

data class ProductCreateCommand(
    val category: Category,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val attribute: ProductAttributeCreateCommand,
)

sealed interface ProductAttributeCreateCommand

data class TransportCreateCommand(
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductAttributeCreateCommand

data class AccommodationCreateCommand(
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
) : ProductAttributeCreateCommand


data class ProductUpdateCommand(
    val id: Long,
    val status: ProductStatus,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val attribute: ProductAttributeUpdateCommand,
)

sealed interface ProductAttributeUpdateCommand

data class TransportUpdateCommand(
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductAttributeUpdateCommand

data class AccommodationUpdateCommand(
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
) : ProductAttributeUpdateCommand