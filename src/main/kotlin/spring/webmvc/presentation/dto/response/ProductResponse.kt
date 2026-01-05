package spring.webmvc.presentation.dto.response

import org.springframework.data.domain.Page
import spring.webmvc.application.dto.result.AccommodationResult
import spring.webmvc.application.dto.result.ProductDetailResult
import spring.webmvc.application.dto.result.ProductSummaryResult
import spring.webmvc.application.dto.result.TransportResult
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.model.enums.ProductStatus
import spring.webmvc.infrastructure.persistence.dto.CursorPage
import java.time.Instant

data class ProductCursorPageResponse(
    val page: CursorPageResponse,
    val products: List<ProductSummaryResponse>,
) {
    companion object {
        fun from(page: CursorPage<ProductSummaryResult>) = ProductCursorPageResponse(
            page = CursorPageResponse.from(page),
            products = page.content.map { ProductSummaryResponse.from(it) }
        )
    }
}

data class ProductOffsetPageResponse(
    val page: OffsetPageResponse,
    val products: List<ProductSummaryResponse>,
) {
    companion object {
        fun from(page: Page<ProductSummaryResult>) = ProductOffsetPageResponse(
            page = OffsetPageResponse.from(page),
            products = page.content.map { ProductSummaryResponse.from(it) }
        )
    }
}

data class ProductSummaryResponse(
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
        fun from(result: ProductSummaryResult) = ProductSummaryResponse(
            id = checkNotNull(result.id),
            category = result.category,
            status = result.status,
            name = result.name,
            description = result.description,
            price = result.price,
            quantity = result.quantity,
            createdAt = result.createdAt,
        )
    }
}

data class ProductDetailResponse(
    val id: Long,
    val category: Category,
    val status: ProductStatus,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val createdAt: Instant,
    val attribute: ProductAttributeResponse,
) {
    companion object {
        fun from(result: ProductDetailResult): ProductDetailResponse {
            val responseDetail = when (result.attribute) {
                is TransportResult -> TransportResponse.from(result.attribute)
                is AccommodationResult -> AccommodationResponse.from(result.attribute)
            }

            return ProductDetailResponse(
                id = result.id,
                category = result.category,
                status = result.status,
                name = result.name,
                description = result.description,
                price = result.price,
                quantity = result.quantity,
                createdAt = result.createdAt,
                attribute = responseDetail,
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
        fun from(result: TransportResult) = TransportResponse(
            departureLocation = result.departureLocation,
            arrivalLocation = result.arrivalLocation,
            departureTime = result.departureTime,
            arrivalTime = result.arrivalTime,
        )
    }
}

data class AccommodationResponse(
    val accommodationId: Long,
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
) : ProductAttributeResponse {
    companion object {
        fun from(result: AccommodationResult) = AccommodationResponse(
            accommodationId = result.accommodationId,
            place = result.place,
            checkInTime = result.checkInTime,
            checkOutTime = result.checkOutTime,
        )
    }
}