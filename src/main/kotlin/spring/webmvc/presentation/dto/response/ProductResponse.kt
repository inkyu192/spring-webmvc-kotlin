package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.AccommodationResult
import spring.webmvc.application.dto.result.ProductDetailResult
import spring.webmvc.application.dto.result.ProductSummaryResult
import spring.webmvc.application.dto.result.TransportResult
import spring.webmvc.domain.model.enums.ProductCategory
import spring.webmvc.domain.model.enums.ProductStatus
import spring.webmvc.domain.model.vo.ProductExposureProperty
import java.time.Instant

data class ProductSummaryResponse(
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
        fun from(result: ProductSummaryResult) = ProductSummaryResponse(
            id = checkNotNull(result.id),
            category = result.category,
            status = result.status,
            name = result.name,
            description = result.description,
            price = result.price,
            quantity = result.quantity,
            exposureProperty = result.exposureProperty,
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
    val exposureProperty: ProductExposureProperty,
    val createdAt: Instant,
    val property: ProductPropertyResponse,
) {
    companion object {
        fun from(result: ProductDetailResult): ProductDetailResponse {
            val responseDetail = when (result.property) {
                is TransportResult -> TransportResponse.from(result.property)
                is AccommodationResult -> AccommodationResponse.from(result.property)
            }

            return ProductDetailResponse(
                id = result.id,
                category = result.category,
                status = result.status,
                name = result.name,
                description = result.description,
                price = result.price,
                quantity = result.quantity,
                exposureProperty = result.exposureProperty,
                createdAt = result.createdAt,
                property = responseDetail,
            )
        }
    }
}

sealed interface ProductPropertyResponse

data class TransportResponse(
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductPropertyResponse {
    companion object {
        fun from(result: TransportResult) = TransportResponse(
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
) : ProductPropertyResponse {
    companion object {
        fun from(result: AccommodationResult) = AccommodationResponse(
            place = result.place,
            checkInTime = result.checkInTime,
            checkOutTime = result.checkOutTime,
        )
    }
}