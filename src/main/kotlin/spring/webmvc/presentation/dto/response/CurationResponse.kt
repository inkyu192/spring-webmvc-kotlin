package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.CurationResult

data class CurationResponse(
    val id: Long,
    val title: String,
) {
    constructor(curationResult: CurationResult) : this(
        id = curationResult.id,
        title = curationResult.title,
    )
}
