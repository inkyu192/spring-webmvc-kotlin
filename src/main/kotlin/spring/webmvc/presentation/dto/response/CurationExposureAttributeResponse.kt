package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.CurationExposureAttributeResult
import spring.webmvc.domain.model.enums.CurationLayout

data class CurationExposureAttributeResponse(
    val layout: CurationLayout,
) {
    companion object {
        fun of(result: CurationExposureAttributeResult) = CurationExposureAttributeResponse(
            layout = result.layout,
        )
    }
}
