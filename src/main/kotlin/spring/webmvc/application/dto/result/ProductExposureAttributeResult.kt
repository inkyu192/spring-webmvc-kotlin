package spring.webmvc.application.dto.result

import spring.webmvc.domain.model.entity.UserProductBadge
import spring.webmvc.domain.model.vo.ProductExposureAttribute

data class ProductExposureAttributeResult(
    val isPromotional: Boolean,
    val isNewArrival: Boolean,
    val isFeatured: Boolean,
    val isLowStock: Boolean,
    val isRecommended: Boolean,
    val isPersonalPick: Boolean,
    val isRecentlyViewed: Boolean,
    val isWished: Boolean,
) {
    companion object {
        fun of(
            vo: ProductExposureAttribute,
            badge: UserProductBadge? = null,
            isRecentlyViewed: Boolean = false,
            isWished: Boolean = false,
        ) = ProductExposureAttributeResult(
            isPromotional = vo.isPromotional,
            isNewArrival = vo.isNewArrival,
            isFeatured = vo.isFeatured,
            isLowStock = vo.isLowStock,
            isRecommended = badge?.isRecommended ?: false,
            isPersonalPick = badge?.isPersonalPick ?: false,
            isRecentlyViewed = isRecentlyViewed,
            isWished = isWished,
        )
    }
}
