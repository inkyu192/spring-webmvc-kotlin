package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Size
import spring.webmvc.application.dto.command.CurationCreateCommand
import spring.webmvc.application.dto.command.CurationProductCreateCommand
import spring.webmvc.domain.model.enums.CurationLayout
import spring.webmvc.domain.model.enums.CurationPlacement
import spring.webmvc.domain.model.enums.CurationType
import spring.webmvc.domain.model.vo.CurationAttribute
import spring.webmvc.domain.model.vo.CurationExposureAttribute

data class CurationCreateRequest(
    val title: String,
    val placement: CurationPlacement,
    val type: CurationType = CurationType.MANUAL,
    val attribute: CurationAttributeRequest = CurationAttributeRequest(),
    val exposureAttribute: CurationExposureAttributeRequest = CurationExposureAttributeRequest(layout = CurationLayout.CAROUSEL),
    val isExposed: Boolean,
    val sortOrder: Long,
    @field:Size(min = 0)
    val products: List<CurationProductCreateRequest> = emptyList(),
) {
    fun toCommand() = CurationCreateCommand(
        title = title,
        placement = placement,
        type = type,
        attribute = attribute.toAttribute(),
        exposureAttribute = exposureAttribute.toAttribute(),
        isExposed = isExposed,
        sortOrder = sortOrder,
        products = products.map { it.toCommand() }.toList()
    )
}

data class CurationProductCreateRequest(
    val productId: Long,
    val sortOrder: Long,
) {
    fun toCommand() = CurationProductCreateCommand(
        productId = productId,
        sortOrder = sortOrder,
    )
}

data class CurationAttributeRequest(
    val keyword: String? = null,
) {
    fun toAttribute() = CurationAttribute(keyword = keyword)
}

data class CurationExposureAttributeRequest(
    val layout: CurationLayout,
) {
    fun toAttribute() = CurationExposureAttribute(layout = layout)
}

