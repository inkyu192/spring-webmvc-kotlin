package spring.webmvc.application.strategy

import org.springframework.stereotype.Component
import spring.webmvc.application.dto.result.FlightResult
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.domain.cache.FlightCache
import spring.webmvc.domain.model.entity.Flight
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.FlightRepository
import spring.webmvc.infrastructure.common.JsonSupport
import spring.webmvc.presentation.exception.EntityNotFoundException

@Component
class FlightStrategy(
    private val flightCache: FlightCache,
    private val flightRepository: FlightRepository,
    private val jsonSupport: JsonSupport,
) : ProductStrategy {
    override fun supports(category: Category) = category == Category.FLIGHT

    override fun findByProductId(productId: Long): ProductResult {
        val cached = flightCache.get(productId)
            ?.let { jsonSupport.readValue(json = it, clazz = FlightResult::class.java) }

        if (cached != null) {
            return cached
        }

        val flightResult = (flightRepository.findByProductId(productId)
            ?.let { FlightResult(flight = it) }
            ?: throw EntityNotFoundException(kClass = Flight::class, id = productId))

        jsonSupport.writeValueAsString(obj = flightResult)
            ?.let { flightCache.set(id = productId, value = it) }

        return flightResult
    }
}