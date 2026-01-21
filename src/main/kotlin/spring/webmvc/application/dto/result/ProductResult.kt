package spring.webmvc.application.dto.result

import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.Transport
import spring.webmvc.domain.model.enums.ProductCategory
import spring.webmvc.domain.model.enums.ProductStatus
import spring.webmvc.domain.model.vo.ProductExposureAttribute
import java.time.Instant

data class ProductSummaryResult(
    val id: Long,
    val category: ProductCategory,
    val status: ProductStatus,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val exposureAttribute: ProductExposureAttribute,
    val createdAt: Instant,
) {
    companion object {
        fun of(product: Product) = ProductSummaryResult(
            id = checkNotNull(product.id),
            category = product.category,
            status = product.status,
            name = product.name,
            description = product.description,
            price = product.price,
            quantity = product.quantity,
            exposureAttribute = product.exposureAttribute,
            createdAt = product.createdAt,
        )
    }
}

data class ProductDetailResult(
    val id: Long,
    val category: ProductCategory,
    val status: ProductStatus,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val exposureAttribute: ProductExposureAttribute,
    val createdAt: Instant,
    val attribute: ProductAttributeResult,
) {
    companion object {
        fun of(
            product: Product,
            attributeResult: ProductAttributeResult,
        ) = ProductDetailResult(
            id = checkNotNull(product.id),
            category = product.category,
            status = product.status,
            name = product.name,
            description = product.description,
            price = product.price,
            quantity = product.quantity,
            exposureAttribute = product.exposureAttribute,
            createdAt = product.createdAt,
            attribute = attributeResult,
        )
    }
}

sealed interface ProductAttributeResult

data class TransportResult(
    val transportId: Long,
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductAttributeResult {
    companion object {
        fun of(transport: Transport) = TransportResult(
            transportId = checkNotNull(transport.id),
            departureLocation = transport.departureLocation,
            arrivalLocation = transport.arrivalLocation,
            departureTime = transport.departureTime,
            arrivalTime = transport.arrivalTime,
        )
    }
}

data class AccommodationResult(
    val accommodationId: Long,
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
) : ProductAttributeResult {
    companion object {
        fun of(accommodation: Accommodation) = AccommodationResult(
            accommodationId = checkNotNull(accommodation.id),
            place = accommodation.place,
            checkInTime = accommodation.checkInTime,
            checkOutTime = accommodation.checkOutTime,
        )
    }
}
