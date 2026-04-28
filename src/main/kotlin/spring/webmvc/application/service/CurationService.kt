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
import spring.webmvc.application.strategy.curation.CurationProductStrategy
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.enums.CurationPlacement
import spring.webmvc.domain.model.enums.CurationType
import spring.webmvc.domain.repository.CurationRepository
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.infrastructure.exception.NotFoundEntityException

@Service
@Transactional(readOnly = true)
class CurationService(
    private val curationRepository: CurationRepository,
    private val productRepository: ProductRepository,
    strategies: List<CurationProductStrategy>,
) {
    private val strategyMap: Map<CurationType, CurationProductStrategy> =
        strategies.associateBy { it.type() }

    @Transactional
    fun createCuration(command: CurationCreateCommand): CurationDetailResult {
        val requestProductIds = command.products.map { it.productId }
        val productMap = productRepository.findAllById(requestProductIds)
            .associateBy { checkNotNull(it.id) }

        val curation = Curation.create(
            title = command.title,
            placement = command.placement,
            type = command.type,
            attribute = command.attribute,
            exposureAttribute = command.exposureAttribute,
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

    @Cacheable(value = ["curations"], key = "'curation:placement:' + #placement")
    fun findCurationsCached(placement: CurationPlacement) = findCurations(placement)

    fun findCurations(placement: CurationPlacement) = curationRepository.findAllByPlacement(placement)
        .map { CurationSummaryResult.of(curation = it) }

    fun findCurationProductWithOffsetPage(id: Long, pageable: Pageable): CurationOffsetPageResult {
        val curation = curationRepository.findById(id)
            ?: throw NotFoundEntityException(kClass = Curation::class, id = id)

        val strategy = checkNotNull(strategyMap[curation.type]) {
            "No strategy found for type: ${curation.type}"
        }

        return strategy.findProductsWithOffsetPage(curation = curation, pageable = pageable)
    }

    @Cacheable(
        value = ["curationProducts"],
        key = "'curation:' + #curationId + ':user:' + #userId + ':cursor:' + #cursorId"
    )
    fun findCurationProductCached(userId: Long?, curationId: Long, cursorId: Long?): CurationCursorPageResult {
        val curation = curationRepository.findById(curationId)
            ?: throw NotFoundEntityException(kClass = Curation::class, id = curationId)

        val strategy = checkNotNull(strategyMap[curation.type]) {
            "No strategy found for type: ${curation.type}"
        }

        return strategy.findProductsWithCursorPage(
            curation = curation,
            userId = userId,
            cursorId = cursorId,
        )
    }
}
