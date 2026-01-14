package spring.webmvc.application.dto.command

import spring.webmvc.domain.model.enums.ProductCategory
import spring.webmvc.domain.model.enums.ProductStatus
import spring.webmvc.domain.model.vo.ProductExposureProperty
import java.time.Instant

data class ProductPutCommand(
    val id: Long?,
    val status: ProductStatus,
    val category: ProductCategory,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val property: ProductPropertyPutCommand,
    val exposureProperty: ProductExposureProperty,
)

sealed interface ProductPropertyPutCommand

data class TransportPutCommand(
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductPropertyPutCommand

data class AccommodationPutCommand(
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
) : ProductPropertyPutCommand