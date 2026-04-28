package spring.webmvc.presentation.dto.request

import spring.webmvc.domain.model.vo.ProductExposureAttribute

data class ProductExposureAttributeRequest(
    val isPromotional: Boolean,
    val isNewArrival: Boolean,
    val isFeatured: Boolean,
    val isLowStock: Boolean,
) {
    fun toVO() = ProductExposureAttribute(
        isPromotional = isPromotional,
        isNewArrival = isNewArrival,
        isFeatured = isFeatured,
        isLowStock = isLowStock,
    )
}
