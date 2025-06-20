package spring.webmvc.application.dto.result

import spring.webmvc.domain.model.entity.Curation

data class CurationResult(
    val id: Long,
    val title: String,
) {
    constructor(curation: Curation) : this(
        id = checkNotNull(curation.id),
        title = curation.title,
    )
}