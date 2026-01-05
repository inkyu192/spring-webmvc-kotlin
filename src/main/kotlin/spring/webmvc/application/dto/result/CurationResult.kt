package spring.webmvc.application.dto.result

import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.entity.CurationProduct
import spring.webmvc.domain.model.enums.Category

data class CurationSummaryResult(
    val id: Long,
    val title: String,
) {
    companion object {
        fun from(curation: Curation) = CurationSummaryResult(
            id = checkNotNull(curation.id),
            title = curation.title,
        )
    }
}

data class CurationDetailResult(
    val id: Long,
    val title: String,
    val products: List<CurationProductResult>,
) {
    companion object {
        fun from(curation: Curation) = CurationDetailResult(
            id = checkNotNull(curation.id),
            title = curation.title,
            products = curation.curationProducts.map { CurationProductResult.from(curationProduct = it) },
        )
    }
}

data class CurationProductResult(
    val category: Category,
    val name: String,
    val description: String,
    val price: Long,
) {
    companion object {
        fun from(curationProduct: CurationProduct) = CurationProductResult(
            category = curationProduct.product.category,
            name = curationProduct.product.name,
            description = curationProduct.product.description,
            price = curationProduct.product.price,
        )
    }
}