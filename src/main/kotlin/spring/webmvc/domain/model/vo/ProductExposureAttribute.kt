package spring.webmvc.domain.model.vo

data class ProductExposureAttribute(
    val isPromotional: Boolean,
    val isNewArrival: Boolean,
    val isFeatured: Boolean,
    val isLowStock: Boolean,
)
