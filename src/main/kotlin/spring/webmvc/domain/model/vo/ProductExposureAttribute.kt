package spring.webmvc.domain.model.vo

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ProductExposureAttribute(
    val isPromotional: Boolean?,
    val isNewArrival: Boolean?,
    val isFeatured: Boolean?,
    val isLowStock: Boolean?,
)