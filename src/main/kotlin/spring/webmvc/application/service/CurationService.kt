package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.command.CurationCreateCommand
import spring.webmvc.application.dto.result.CurationProductResult
import spring.webmvc.application.dto.result.CurationResult
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.repository.CurationCacheRepository
import spring.webmvc.domain.repository.CurationProductRepository
import spring.webmvc.domain.repository.CurationRepository
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.domain.service.CurationDomainService
import spring.webmvc.presentation.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class CurationService(
    private val curationDomainService: CurationDomainService,
    private val curationRepository: CurationRepository,
    private val curationProductRepository: CurationProductRepository,
    private val productRepository: ProductRepository,
    private val curationCacheRepository: CurationCacheRepository,
) {
    @Transactional
    fun createCuration(command: CurationCreateCommand): Long {
        val requestProductIds = command.products.map { it.productId }
        val products = productRepository.findByIds(requestProductIds)

        val curation = curationRepository.save(
            curationDomainService.createCuration(
                title = command.title,
                isExposed = command.isExposed,
                sortOrder = command.sortOrder,
                requestProductIds = requestProductIds,
                products = products,
            )
        ).apply { curationCacheRepository.delete() }

        return checkNotNull(curation.id)
    }

    fun findCurations(): List<CurationResult> {
        val cached = curationCacheRepository.getCurations()
        if (cached.isNotEmpty()) {
            return cached.map { CurationResult(curation = it) }
        }

        val curations = curationRepository.findExposed()

        curationCacheRepository.setCurations(
            curations.map {
                curationDomainService.createCurationCache(curation = it)
            }
        )

        return curations.map { CurationResult(curation = it) }
    }

    fun findCurationProduct(curationId: Long, cursorId: Long?, size: Int): CurationProductResult {
        val cached = curationCacheRepository.getCurationProducts(curationId, cursorId, size)
        if (cached != null) {
            return CurationProductResult(curationProduct = cached)
        }

        val curation = curationRepository.findById(id = curationId)
            ?: throw EntityNotFoundException(kClass = Curation::class, id = curationId)

        val curationProductPage = curationProductRepository.findAll(
            curationId = curationId,
            cursorId = cursorId,
            size = size,
        )

        curationCacheRepository.setCurationProducts(
            curationId = curationId,
            cursorId = cursorId,
            size = size,
            cache = curationDomainService.createCurationProductCache(
                curation = curation,
                productPage = curationProductPage.map { it.product },
            )
        )

        return CurationProductResult(curation = curation, productPage = curationProductPage.map { it.product })
    }
}