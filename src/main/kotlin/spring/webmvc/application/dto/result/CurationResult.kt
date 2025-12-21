package spring.webmvc.application.dto.result

import spring.webmvc.domain.model.cache.CurationCache
import spring.webmvc.domain.model.cache.CurationProductCache
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.infrastructure.persistence.dto.CursorPage

data class CurationResult(
    val id: Long,
    val title: String,
) {
    constructor(curation: Curation) : this(
        id = checkNotNull(curation.id),
        title = curation.title,
    )

    constructor(curation: CurationCache) : this(
        id = curation.id,
        title = curation.title,
    )
}

data class CurationProductResult(
    val curation: CurationResult,
    val productPage: CursorPage<ProductResult>,
) {
    constructor(curation: Curation, productPage: CursorPage<Product>) : this(
        curation = CurationResult(curation),
        productPage = productPage.map { ProductResult(it) },
    )

    constructor(curationProduct: CurationProductCache) : this(
        curation = CurationResult(curationProduct.curation),
        productPage = curationProduct.productPage.map { ProductResult(it) },
    )
}