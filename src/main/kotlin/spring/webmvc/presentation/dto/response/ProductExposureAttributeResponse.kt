package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.ProductExposureAttributeResult

data class ProductExposureAttributeResponse(
    val isPromotional: Boolean,
    val isNewArrival: Boolean,
    val isFeatured: Boolean,
    val isLowStock: Boolean,
    val isRecommended: Boolean,
    val isPersonalPick: Boolean,
) {
    companion object {
        fun of(result: ProductExposureAttributeResult) = ProductExposureAttributeResponse(
            isPromotional = result.isPromotional,
            isNewArrival = result.isNewArrival,
            isFeatured = result.isFeatured,
            isLowStock = result.isLowStock,
            isRecommended = result.isRecommended,
            isPersonalPick = result.isPersonalPick,
        )
    }
}
