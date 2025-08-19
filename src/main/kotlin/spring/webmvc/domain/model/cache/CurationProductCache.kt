package spring.webmvc.domain.model.cache

import spring.webmvc.infrastructure.persistence.dto.CursorPage

data class CurationCache(
    val id: Long,
    val title: String,
) {
    companion object {
        fun create(id: Long, title: String) =
            CurationCache(
                id = id,
                title = title,
            )
    }
}

data class CurationProductCache(
    val curation: CurationCache,
    val productPage: CursorPage<ProductCache>,
)