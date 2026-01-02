package spring.webmvc.application.dto.result

import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.enums.CurationCategory
import spring.webmvc.infrastructure.persistence.dto.CursorPage

data class CurationResult(
    val id: Long,
    val title: String,
    val category: CurationCategory,
) {
    companion object {
        fun from(curation: Curation) = CurationResult(
            id = checkNotNull(curation.id),
            title = curation.title,
            category = curation.category,
        )
    }
}

data class CurationProductResult(
    val curation: CurationResult,
    val productPage: CursorPage<ProductResult>,
)