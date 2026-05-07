package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.WishlistResult
import java.time.Instant

data class WishlistResponse(
    val id: Long,
    val product: ProductSummaryResponse,
    val createdAt: Instant,
) {
    companion object {
        fun of(result: WishlistResult) = WishlistResponse(
            id = result.id,
            product = ProductSummaryResponse.of(result.product),
            createdAt = result.createdAt,
        )
    }
}
