package spring.webmvc.application.dto.command

import spring.webmvc.domain.model.enums.ProductCategory
import spring.webmvc.domain.model.enums.ProductStatus
import spring.webmvc.domain.model.vo.ProductExposureAttribute
import java.time.Instant

data class ProductCreateCommand(
    val category: ProductCategory,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val attribute: ProductAttributePutCommand,
    val exposureAttribute: ProductExposureAttribute,
)

data class ProductUpdateCommand(
    val id: Long,
    val status: ProductStatus,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val attribute: ProductAttributePutCommand,
    val exposureAttribute: ProductExposureAttribute,
)

sealed interface ProductAttributePutCommand

data class TransportPutCommand(
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductAttributePutCommand

data class AccommodationPutCommand(
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
) : ProductAttributePutCommand
