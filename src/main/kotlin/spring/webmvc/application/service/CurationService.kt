package spring.webmvc.application.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.command.CurationCreateCommand
import spring.webmvc.application.dto.result.CurationDetailResult
import spring.webmvc.application.dto.result.CurationProductResult
import spring.webmvc.application.dto.result.CurationSummaryResult
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.enums.CurationCategory
import spring.webmvc.domain.repository.CurationProductRepository
import spring.webmvc.domain.repository.CurationRepository
import spring.webmvc.domain.repository.ProductRepository

@Service
@Transactional(readOnly = true)
class CurationService(
    private val curationRepository: CurationRepository,
    private val curationProductRepository: CurationProductRepository,
    private val productRepository: ProductRepository,
) {
    @Transactional
    fun createCuration(command: CurationCreateCommand): CurationDetailResult {
        val requestProductIds = command.products.map { it.productId }
        val productsMap = productRepository.findAllById(requestProductIds)
            .associateBy { checkNotNull(it.id) }

        val curation = Curation.create(
            title = command.title,
            category = command.category,
            isExposed = command.isExposed,
            sortOrder = command.sortOrder,
        )

        command.products.forEach { (productId, sortOrder) ->
            productsMap[productId]?.let { product ->
                curation.addProduct(
                    product = product,
                    sortOrder = sortOrder,
                )
            }
        }

        curationRepository.save(curation)

        return CurationDetailResult.from(curation)
    }

    @Cacheable(value = ["curations"], key = "'curations:' + #category")
    fun findCurationsCached(category: CurationCategory) = findCurations(category)

    fun findCurations(category: CurationCategory) = curationRepository.findAllByCategory(category)
        .map { CurationSummaryResult.from(curation = it) }

    @Cacheable(value = ["curationProducts"], key = "'curations:' + #curationId + ':' + #cursorId")
    fun findCurationProductWithCursorPageCached(curationId: Long, cursorId: Long?) =
        findCurationProductWithCursorPage(curationId, cursorId)

    fun findCurationProductWithCursorPage(curationId: Long, cursorId: Long?) =
        curationProductRepository.findAllWithCursorPage(
            curationId = curationId,
            cursorId = cursorId,
        ).map {
            CurationProductResult(
                category = it.product.category,
                name = it.product.name,
                description = it.product.description,
                price = it.product.price,
            )
        }

    fun findCurationProductWithOffsetPage(curationId: Long, pageable: Pageable): Page<CurationProductResult> =
        curationProductRepository.findAllWithOffsetPage(
            curationId = curationId,
            pageable = pageable,
        ).map {
            CurationProductResult(
                category = it.product.category,
                name = it.product.name,
                description = it.product.description,
                price = it.product.price,
            )
        }
}