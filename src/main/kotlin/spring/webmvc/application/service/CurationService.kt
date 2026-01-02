package spring.webmvc.application.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.command.CurationCreateCommand
import spring.webmvc.application.dto.result.CurationProductResult
import spring.webmvc.application.dto.result.CurationResult
import spring.webmvc.application.strategy.ProductStrategy
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.model.enums.CurationCategory
import spring.webmvc.domain.repository.CurationProductRepository
import spring.webmvc.domain.repository.CurationRepository
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.domain.service.CurationDomainService

@Service
@Transactional(readOnly = true)
class CurationService(
    private val curationDomainService: CurationDomainService,
    private val curationRepository: CurationRepository,
    private val curationProductRepository: CurationProductRepository,
    private val productStrategyMap: Map<Category, ProductStrategy>,
    private val productRepository: ProductRepository,
) {
    @Transactional
    fun createCuration(command: CurationCreateCommand): CurationResult {
        val requestProductIds = command.products.map { it.productId }
        val productsMap = productRepository.findAllById(requestProductIds)
            .associateBy { checkNotNull(it.id) }

        val productsWithSortOrder = command.products.mapNotNull { productCommand ->
            productsMap[productCommand.productId]?.let { product ->
                product to productCommand.sortOrder
            }
        }

        val curation = curationDomainService.createCuration(
            title = command.title,
            category = command.category,
            isExposed = command.isExposed,
            sortOrder = command.sortOrder,
            products = productsWithSortOrder,
        )

        curationRepository.save(curation)

        return CurationResult.from(curation)
    }

    @Cacheable(value = ["curations"], key = "'curations:' + #category")
    fun findCurations(category: CurationCategory) = curationRepository.findByCategory(category)
        .map { CurationResult.from(it) }

    @Cacheable(value = ["curationProducts"], key = "'curations:' + #curationId + ':' + #cursorId")
    fun findCurationProduct(curationId: Long, cursorId: Long?): CurationProductResult {
        val curation = curationRepository.findById(id = curationId)

        val productResultPage = curationProductRepository.findAll(
            curation = curation,
            cursorId = cursorId,
        ).map { curationProduct ->
            val product = curationProduct.product
            val strategy = productStrategyMap[product.category]
                ?: throw UnsupportedOperationException("${product.category}")

            strategy.findByProductId(checkNotNull(product.id))
        }

        return CurationProductResult(
            curation = CurationResult.from(curation),
            productPage = productResultPage,
        )
    }
}