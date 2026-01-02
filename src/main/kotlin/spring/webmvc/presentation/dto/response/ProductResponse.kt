package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.AccommodationResult
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.application.dto.result.TransportResult
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.Transport
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.infrastructure.persistence.dto.CursorPage
import java.time.Instant

data class ProductPageResponse(
    val page: CursorPageResponse,
    val products: List<ProductListResponse>,
) {
    companion object {
        fun from(page: CursorPage<Product>): ProductPageResponse {
            return ProductPageResponse(
                page = CursorPageResponse.from(page),
                products = page.content.map { ProductListResponse.from(it) }
            )
        }
    }
}

data class ProductListResponse(
    val id: Long,
    val category: Category,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val createdAt: Instant,
) {
    companion object {
        fun from(product: Product): ProductListResponse {
            return ProductListResponse(
                id = checkNotNull(product.id),
                category = product.category,
                name = product.name,
                description = product.description,
                price = product.price,
                quantity = product.quantity,
                createdAt = product.createdAt,
            )
        }
    }
}

data class ProductResponse(
    val id: Long,
    val category: Category,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val createdAt: Instant,
    val detail: ProductDetailResponse,
) {
    companion object {
        fun from(result: ProductResult): ProductResponse {
            val responseDetail = when (result.detail) {
                is TransportResult -> TransportResponse.from(result.detail)
                is AccommodationResult -> AccommodationResponse.from(result.detail)
            }

            return ProductResponse(
                id = result.id,
                category = result.category,
                name = result.name,
                description = result.description,
                price = result.price,
                quantity = result.quantity,
                createdAt = result.createdAt,
                detail = responseDetail,
            )
        }
    }
}

sealed interface ProductDetailResponse

data class TransportResponse(
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductDetailResponse {
    companion object {
        fun from(detail: TransportResult): TransportResponse {
            return TransportResponse(
                departureLocation = detail.departureLocation,
                arrivalLocation = detail.arrivalLocation,
                departureTime = detail.departureTime,
                arrivalTime = detail.arrivalTime,
            )
        }

        fun from(transport: Transport): TransportResponse {
            return TransportResponse(
                departureLocation = transport.departureLocation,
                arrivalLocation = transport.arrivalLocation,
                departureTime = transport.departureTime,
                arrivalTime = transport.arrivalTime,
            )
        }
    }
}

data class AccommodationResponse(
    val accommodationId: Long,
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
) : ProductDetailResponse {
    companion object {
        fun from(detail: AccommodationResult): AccommodationResponse {
            return AccommodationResponse(
                accommodationId = detail.accommodationId,
                place = detail.place,
                checkInTime = detail.checkInTime,
                checkOutTime = detail.checkOutTime,
            )
        }

        fun from(accommodation: Accommodation): AccommodationResponse {
            return AccommodationResponse(
                accommodationId = checkNotNull(accommodation.id),
                place = accommodation.place,
                checkInTime = accommodation.checkInTime,
                checkOutTime = accommodation.checkOutTime,
            )
        }
    }
}