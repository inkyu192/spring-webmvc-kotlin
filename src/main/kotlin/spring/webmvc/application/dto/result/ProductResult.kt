package spring.webmvc.application.dto.result

import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.Transport
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.model.enums.ProductStatus
import java.time.Instant

data class ProductSummaryResult(
    val id: Long,
    val category: Category,
    val status: ProductStatus,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
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
            createdAt = product.createdAt,
        )
    }
}

data class ProductDetailResult(
    val id: Long,
    val category: Category,
    val status: ProductStatus,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val createdAt: Instant,
    val attribute: ProductAttributeResult,
) {
    companion object {
        fun from(
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
) : ProductAttributeResult {
    companion object {
        fun from(accommodation: Accommodation) = AccommodationResult(
            accommodationId = checkNotNull(accommodation.id),
            place = accommodation.place,
            checkInTime = accommodation.checkInTime,
            checkOutTime = accommodation.checkOutTime,
        )
    }
}