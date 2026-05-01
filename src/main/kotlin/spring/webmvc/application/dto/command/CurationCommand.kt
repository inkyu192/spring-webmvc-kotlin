package spring.webmvc.application.dto.command

import spring.webmvc.domain.model.enums.CurationLayout
import spring.webmvc.domain.model.enums.CurationPlacement
import spring.webmvc.domain.model.enums.CurationType
import spring.webmvc.domain.model.vo.CurationAttribute
import spring.webmvc.domain.model.vo.CurationExposureAttribute

data class CurationCreateCommand(
    val title: String,
    val placement: CurationPlacement,
    val type: CurationType = CurationType.MANUAL,
    val attribute: CurationAttribute = CurationAttribute(tagIds = emptyList()),
    val exposureAttribute: CurationExposureAttribute = CurationExposureAttribute(layout = CurationLayout.CAROUSEL),
    val isExposed: Boolean,
    val sortOrder: Long,
    val products: List<CurationProductCreateCommand>,
)

data class CurationProductCreateCommand(
    val productId: Long,
    val sortOrder: Long,
)
