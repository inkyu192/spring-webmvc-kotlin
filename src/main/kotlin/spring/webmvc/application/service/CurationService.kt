package spring.webmvc.application.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.command.CurationCreateCommand
import spring.webmvc.application.dto.result.CurationCursorPageResult
import spring.webmvc.application.dto.result.CurationDetailResult
import spring.webmvc.application.dto.result.CurationOffsetPageResult
import spring.webmvc.application.dto.result.CurationSummaryResult
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.enums.CurationCategory
import spring.webmvc.domain.repository.CurationProductRepository
import spring.webmvc.domain.repository.CurationRepository
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.domain.repository.UserCurationProductRepository

@Service
@Transactional(readOnly = true)
class CurationService(
    private val curationRepository: CurationRepository,
    private val curationProductRepository: CurationProductRepository,
    private val productRepository: ProductRepository,
    private val userCurationProductRepository: UserCurationProductRepository,
) {
    @Transactional
    fun createCuration(command: CurationCreateCommand): CurationDetailResult {
        val requestProductIds = command.products.map { it.productId }
        val productMap = productRepository.findAllById(requestProductIds)
            .associateBy { checkNotNull(it.id) }

        val curation = Curation.create(
            title = command.title,
            category = command.category,
            isExposed = command.isExposed,
            sortOrder = command.sortOrder,
        )

        command.products.forEach { (productId, sortOrder) ->
            productMap[productId]?.let { product ->
                curation.addProduct(
                    product = product,
                    sortOrder = sortOrder,
                )
            }
        }

        curationRepository.save(curation)

        return CurationDetailResult.of(curation)
    }

    @Cacheable(value = ["curations"], key = "'curation:category:' + #category")
    fun findCurationsCached(category: CurationCategory) = findCurations(category)

    fun findCurations(category: CurationCategory) = curationRepository.findAllByCategory(category)
        .map { CurationSummaryResult.of(curation = it) }

    fun findCurationProductWithOffsetPage(id: Long, pageable: Pageable): CurationOffsetPageResult {
        val curation = curationRepository.findById(id)
        val page = curationProductRepository.findAllWithOffsetPage(
            curationId = id,
            pageable = pageable,
        )

        return CurationOffsetPageResult.of(curation = curation, page = page)
    }

    @Cacheable(
        value = ["curationProducts"],
        key = "'curation:' + #curationId + ':user:' + #userId + ':cursor:' + #cursorId"
    )
    fun findCurationProductCached(userId: Long?, curationId: Long, cursorId: Long?): CurationCursorPageResult {
        if (userId == null) {
            return findCurationProductWithCursorPage(curationId = curationId, cursorId = cursorId)
        }

        return findUserCurationProduct(userId = userId, curationId = curationId)
            ?: findCurationProductWithCursorPage(curationId = curationId, cursorId = cursorId)
    }


    fun findCurationProductWithCursorPage(curationId: Long, cursorId: Long?): CurationCursorPageResult {
        val curation = curationRepository.findById(curationId)
        val page = curationProductRepository.findAllWithCursorPage(
            curationId = curationId,
            cursorId = cursorId,
        )

        return CurationCursorPageResult.of(curation = curation, page = page)
    }

    fun findUserCurationProduct(userId: Long, curationId: Long): CurationCursorPageResult? {
        val userCurationProduct = userCurationProductRepository.findByUserIdAndCurationId(
            userId = userId,
            curationId = curationId,
        ) ?: return null

        val curation = curationRepository.findById(curationId)
        val productIds = userCurationProduct.productIds
        val productMap = productRepository.findAllById(productIds)
            .associateBy { checkNotNull(it.id) }

        val products = productIds.mapNotNull { productMap[it] }

        return CurationCursorPageResult.of(curation = curation, products = products)
    }
}
