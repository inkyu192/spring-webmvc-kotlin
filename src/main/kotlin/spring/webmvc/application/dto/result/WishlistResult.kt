package spring.webmvc.application.dto.result

import spring.webmvc.domain.model.entity.Wishlist
import java.time.Instant

data class WishlistResult(
    val id: Long,
    val product: ProductSummaryResult,
    val createdAt: Instant,
) {
    companion object {
        fun of(wishlist: Wishlist) = WishlistResult(
            id = checkNotNull(wishlist.id),
            product = ProductSummaryResult.of(product = wishlist.product, isWished = true),
            createdAt = wishlist.createdAt,
        )
    }
}
