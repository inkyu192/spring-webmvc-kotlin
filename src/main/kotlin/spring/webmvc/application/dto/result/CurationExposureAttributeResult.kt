package spring.webmvc.application.dto.result

import spring.webmvc.domain.model.enums.CurationLayout
import spring.webmvc.domain.model.vo.CurationExposureAttribute

data class CurationExposureAttributeResult(
    val layout: CurationLayout,
) {
    companion object {
        fun of(vo: CurationExposureAttribute) = CurationExposureAttributeResult(
            layout = vo.layout,
        )
    }
}
