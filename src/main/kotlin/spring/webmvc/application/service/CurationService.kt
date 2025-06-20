package spring.webmvc.application.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.command.CurationCreateCommand
import spring.webmvc.application.dto.result.CurationResult
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.domain.cache.CacheKey
import spring.webmvc.domain.cache.ZSetCache
import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.repository.CurationProductRepository
import spring.webmvc.domain.repository.CurationRepository
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.presentation.exception.EntityNotFoundException
import java.time.Duration

@Service
@Transactional(readOnly = true)
class CurationService(
    private val curationRepository: CurationRepository,
    private val curationProductRepository: CurationProductRepository,
    private val productRepository: ProductRepository,
    private val zSetCache: ZSetCache,
) {
    @Transactional
    fun createCuration(command: CurationCreateCommand): CurationResult {
        val productMap = productRepository.findAllById(ids = command.products.map { it.productId })
            .associateBy { it.id }

        val curation = Curation.create(
            title = command.title,
            isExposed = command.isExposed,
            sortOrder = command.sortOrder
        )

        command.products.forEach { productCommand ->
            val product = productMap[productCommand.productId]
                ?: throw EntityNotFoundException(kClass = Product::class, id = productCommand.productId)

            curation.addProduct(product = product, sortOrder = productCommand.sortOrder)
        }

        curationRepository.save(curation)
        return CurationResult(curation)
    }

    fun findCurations(): List<CurationResult> {
        val key = CacheKey.CURATION.generate()
        val cache = zSetCache.range(key = key, start = 0, end = -1, clazz = CurationResult::class.java)

        if (cache.isNotEmpty()) {
            return cache.toList()
        }

        val result = curationRepository.findExposed()
            .map { curation ->
                val curationResult = CurationResult(curation)
                zSetCache.add(key = key, value = curationResult, score = curation.sortOrder.toDouble())
                curationResult
            }

        zSetCache.expire(key, Duration.ofHours(1))
        return result
    }

    fun findCurationProduct(pageable: Pageable, id: Long): Page<ProductResult> {
        val key = CacheKey.CURATION_PRODUCT.generate(id)

        val start = pageable.offset
        val end = start + pageable.pageSize - 1

        val size = zSetCache.size(key)
        val cacheContent = zSetCache.range(key = key, start = start, end = end, clazz = ProductResult::class.java)

        if (cacheContent.isNotEmpty()) {
            return PageImpl(cacheContent.toList(), pageable, size)
        }

        val result = curationProductRepository.findAllByCurationId(pageable, id)
            .map { curationProduct ->
                val productResult = ProductResult(curationProduct.product)
                zSetCache.add(key = key, value = productResult, score = curationProduct.sortOrder.toDouble())
                productResult
            }

        zSetCache.expire(key = key, timeout = Duration.ofHours(1))
        return result
    }
}