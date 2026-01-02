package spring.webmvc.application.dto.result

import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.entity.Transport
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

data class ProductResult(
    val id: Long,
    val category: Category,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val createdAt: Instant,
    val detail: ProductResultDetail,
) {
    companion object {
        fun from(transport: Transport): ProductResult {
            return ProductResult(
                id = checkNotNull(transport.product.id),
                category = Category.TRANSPORT,
                name = transport.product.name,
                description = transport.product.description,
                price = transport.product.price,
                quantity = transport.product.quantity,
                createdAt = transport.product.createdAt,
                detail = TransportResult.from(transport),
            )
        }

        fun from(accommodation: Accommodation): ProductResult {
            return ProductResult(
                id = checkNotNull(accommodation.product.id),
                category = Category.ACCOMMODATION,
                name = accommodation.product.name,
                description = accommodation.product.description,
                price = accommodation.product.price,
                quantity = accommodation.product.quantity,
                createdAt = accommodation.product.createdAt,
                detail = AccommodationResult.from(accommodation),
            )
        }
    }
}

sealed interface ProductResultDetail

data class TransportResult(
    val transportId: Long,
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductResultDetail {
    companion object {
        fun from(transport: Transport): TransportResult {
            return TransportResult(
                transportId = checkNotNull(transport.id),
                departureLocation = transport.departureLocation,
                arrivalLocation = transport.arrivalLocation,
                departureTime = transport.departureTime,
                arrivalTime = transport.arrivalTime,
            )
        }
    }
}

data class AccommodationResult(
    val accommodationId: Long,
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
) : ProductResultDetail {
    companion object {
        fun from(accommodation: Accommodation): AccommodationResult {
            return AccommodationResult(
                accommodationId = checkNotNull(accommodation.id),
                place = accommodation.place,
                checkInTime = accommodation.checkInTime,
                checkOutTime = accommodation.checkOutTime,
            )
        }
    }
}