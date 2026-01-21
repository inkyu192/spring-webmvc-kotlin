package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.AccommodationResult
import spring.webmvc.application.dto.result.ProductDetailResult
import spring.webmvc.application.dto.result.ProductSummaryResult
import spring.webmvc.application.dto.result.TransportResult
import spring.webmvc.domain.model.enums.ProductCategory
import spring.webmvc.domain.model.enums.ProductStatus
import spring.webmvc.domain.model.vo.ProductExposureAttribute
import java.time.Instant

data class ProductSummaryResponse(
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
        fun of(result: ProductSummaryResult) = ProductSummaryResponse(
            id = checkNotNull(result.id),
            category = result.category,
            status = result.status,
            name = result.name,
            description = result.description,
            price = result.price,
            quantity = result.quantity,
            exposureAttribute = result.exposureAttribute,
            createdAt = result.createdAt,
        )
    }
}

data class ProductDetailResponse(
    val id: Long,
    val category: ProductCategory,
    val status: ProductStatus,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val exposureAttribute: ProductExposureAttribute,
    val createdAt: Instant,
    val attribute: ProductAttributeResponse,
) {
    companion object {
        fun of(result: ProductDetailResult): ProductDetailResponse {
            val responseAttribute = when (result.attribute) {
                is TransportResult -> TransportResponse.of(result.attribute)
                is AccommodationResult -> AccommodationResponse.of(result.attribute)
            }

            return ProductDetailResponse(
                id = result.id,
                category = result.category,
                status = result.status,
                name = result.name,
                description = result.description,
                price = result.price,
                quantity = result.quantity,
                exposureAttribute = result.exposureAttribute,
                createdAt = result.createdAt,
                attribute = responseAttribute,
            )
        }
    }
}

sealed interface ProductAttributeResponse

data class TransportResponse(
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductAttributeResponse {
    companion object {
        fun of(result: TransportResult) = TransportResponse(
            departureLocation = result.departureLocation,
            arrivalLocation = result.arrivalLocation,
            departureTime = result.departureTime,
            arrivalTime = result.arrivalTime,
        )
    }
}

data class AccommodationResponse(
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
) : ProductAttributeResponse {
    companion object {
        fun of(result: AccommodationResult) = AccommodationResponse(
            place = result.place,
            checkInTime = result.checkInTime,
            checkOutTime = result.checkOutTime,
        )
    }
}
