package spring.webmvc.application.strategy

import org.springframework.stereotype.Component
import spring.webmvc.application.dto.result.AccommodationResult
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.domain.cache.AccommodationCache
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.AccommodationRepository
import spring.webmvc.infrastructure.common.JsonSupport
import spring.webmvc.presentation.exception.EntityNotFoundException

@Component
class AccommodationStrategy(
    private val accommodationCache: AccommodationCache,
    private val accommodationRepository: AccommodationRepository,
    private val jsonSupport: JsonSupport,
) : ProductStrategy {
    override fun supports(category: Category) = category == Category.ACCOMMODATION

    override fun findByProductId(productId: Long): ProductResult {
        val cache = accommodationCache.get(productId)
            ?.let { jsonSupport.readValue(json = it, clazz = AccommodationResult::class.java) }

        if (cache != null) {
            return cache
        }

        val accommodationResult = accommodationRepository.findByProductId(productId)
            ?.let { AccommodationResult(accommodation = it) }
            ?: throw EntityNotFoundException(kClass = AccommodationRepository::class, id = productId)

        jsonSupport.writeValueAsString(obj = accommodationResult)
            ?.let { accommodationCache.set(id = productId, value = it) }

        return accommodationResult
    }
}