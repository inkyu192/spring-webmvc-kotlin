package spring.webmvc.domain.service

import org.springframework.stereotype.Service
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.entity.CurationProduct
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.CurationCategory

@Service
class CurationDomainService {
    fun createCuration(
        title: String,
        category: CurationCategory,
        isExposed: Boolean,
        sortOrder: Long,
        products: List<Pair<Product, Long>>,
    ): Curation {
        val curation = Curation.create(
            title = title,
            category = category,
            isExposed = isExposed,
            sortOrder = sortOrder,
        )

        products.forEach { (product, productSortOrder) ->
            val curationProduct = CurationProduct.create(
                curation = curation,
                product = product,
                sortOrder = productSortOrder,
            )

            curation.addProduct(curationProduct)
        }

        return curation
    }
}