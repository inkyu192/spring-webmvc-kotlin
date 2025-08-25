package spring.webmvc.domain.model.cache

import spring.webmvc.infrastructure.persistence.dto.CursorPage

data class CurationProductCache(
    val curation: CurationCache,
    val productPage: CursorPage<ProductCache>,
)