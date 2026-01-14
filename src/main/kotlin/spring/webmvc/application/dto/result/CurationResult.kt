package spring.webmvc.application.dto.result

import org.springframework.data.domain.Page
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.entity.CurationProduct
import spring.webmvc.domain.model.enums.CurationCategory
import spring.webmvc.infrastructure.persistence.dto.CursorPage

data class CurationSummaryResult(
    val id: Long,
    val title: String,
    val category: CurationCategory,
) {
    companion object {
        fun from(curation: Curation) = CurationSummaryResult(
            id = checkNotNull(curation.id),
            title = curation.title,
            category = curation.category,
        )
    }
}

data class CurationDetailResult(
    val id: Long,
    val title: String,
    val category: CurationCategory,
    val products: List<CurationProductResult>,
) {
    companion object {
        fun from(curation: Curation) = CurationDetailResult(
            id = checkNotNull(curation.id),
            title = curation.title,
            category = curation.category,
            products = curation.curationProducts.map { CurationProductResult.from(curationProduct = it) },
        )
    }
}

data class CurationCursorPageResult(
    val id: Long,
    val title: String,
    val category: CurationCategory,
    val productPage: CursorPage<CurationProductResult>,
) {
    companion object {
        fun from(
            curation: Curation,
            page: CursorPage<CurationProduct>,
        ) = CurationCursorPageResult(
            id = checkNotNull(curation.id),
            title = curation.title,
            category = curation.category,
            productPage = page.map { CurationProductResult.from(curationProduct = it) },
        )
    }
}

data class CurationOffsetPageResult(
    val id: Long,
    val title: String,
    val category: CurationCategory,
    val productPage: Page<CurationProductResult>,
) {
    companion object {
        fun from(
            curation: Curation,
            page: Page<CurationProduct>,
        ) = CurationOffsetPageResult(
            id = checkNotNull(curation.id),
            title = curation.title,
            category = curation.category,
            productPage = page.map { CurationProductResult.from(curationProduct = it) },
        )
    }
}

data class CurationProductResult(
    val id: Long,
    val name: String,
    val description: String,
    val price: Long,
) {
    companion object {
        fun from(curationProduct: CurationProduct) = CurationProductResult(
            id = checkNotNull(curationProduct.id),
            name = curationProduct.product.name,
            description = curationProduct.product.description,
            price = curationProduct.product.price,
        )
    }
}