package spring.webmvc.application.dto.result

import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.Transport
import spring.webmvc.domain.model.enums.ProductCategory
import spring.webmvc.domain.model.enums.ProductStatus
import spring.webmvc.domain.model.vo.ProductExposureProperty
import java.time.Instant

data class ProductSummaryResult(
    val id: Long,
    val category: ProductCategory,
    val status: ProductStatus,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val exposureProperty: ProductExposureProperty,
    val createdAt: Instant,
) {
    companion object {
        fun from(product: Product) = ProductSummaryResult(
            id = checkNotNull(product.id),
            category = product.category,
            status = product.status,
            name = product.name,
            description = product.description,
            price = product.price,
            quantity = product.quantity,
            exposureProperty = product.exposureProperty,
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
    val exposureProperty: ProductExposureProperty,
    val createdAt: Instant,
    val property: ProductPropertyResult,
) {
    companion object {
        fun from(
            product: Product,
            propertyResult: ProductPropertyResult,
        ) = ProductDetailResult(
            id = checkNotNull(product.id),
            category = product.category,
            status = product.status,
            name = product.name,
            description = product.description,
            price = product.price,
            quantity = product.quantity,
            exposureProperty = product.exposureProperty,
            createdAt = product.createdAt,
            property = propertyResult,
        )
    }
}

sealed interface ProductPropertyResult

data class TransportResult(
    val transportId: Long,
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductPropertyResult {
    companion object {
        fun from(transport: Transport) = TransportResult(
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
) : ProductPropertyResult {
    companion object {
        fun from(accommodation: Accommodation) = AccommodationResult(
            accommodationId = checkNotNull(accommodation.id),
            place = accommodation.place,
            checkInTime = accommodation.checkInTime,
            checkOutTime = accommodation.checkOutTime,
        )
    }
}