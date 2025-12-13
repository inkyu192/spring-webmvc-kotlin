package spring.webmvc.domain.service

import org.springframework.stereotype.Service
import spring.webmvc.domain.model.cache.CurationCache
import spring.webmvc.domain.model.cache.CurationProductCache
import spring.webmvc.domain.model.cache.ProductCache
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.entity.CurationProduct
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.infrastructure.exception.EntityNotFoundException
import spring.webmvc.infrastructure.persistence.dto.CursorPage

@Service
class CurationDomainService {
    fun createCuration(
        title: String,
        isExposed: Boolean,
        sortOrder: Long,
        requestProductIds: List<Long>,
        products: List<Product>,
    ): Curation {
        validateProducts(requestProductIds = requestProductIds, products = products)

        val curation = Curation.create(
            title = title,
            isExposed = isExposed,
            sortOrder = sortOrder,
        )

        addProduct(curation = curation, products = products, requestProductIds = requestProductIds)

        return curation
    }

    private fun validateProducts(requestProductIds: List<Long>, products: List<Product>) {
        val existingProductIds = products.mapNotNull { it.id }.toSet()
        val missingProductIds = requestProductIds - existingProductIds

        if (missingProductIds.isNotEmpty()) {
            throw EntityNotFoundException(kClass = Product::class, id = missingProductIds.first())
        }
    }

    private fun addProduct(
        curation: Curation,
        products: List<Product>,
        requestProductIds: List<Long>,
    ) {
        requestProductIds.forEach { requestProductId ->
            val product = products.firstOrNull { it.id == requestProductId }

            if (product != null) {
                curation.addProduct(
                    curationProduct = CurationProduct.create(
                        curation = curation,
                        product = product
                    )
                )
            }
        }
    }

    fun createCurationCache(curation: Curation) =
        CurationCache.create(id = checkNotNull(curation.id), title = curation.title)

    fun createCurationProductCache(curation: Curation, productPage: CursorPage<Product>): CurationProductCache {
        val curation = createCurationCache(curation = curation)
        val productPage = productPage.map {
            ProductCache.create(
                id = checkNotNull(it.id),
                price = it.price,
                category = it.category,
                createdAt = it.createdAt,
                quantity = it.quantity,
                description = it.description,
                name = it.name,
            )
        }

        return CurationProductCache(curation = curation, productPage = productPage)
    }
}