package spring.webmvc.application.strategy

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.FlightCreateCommand
import spring.webmvc.application.dto.command.ProductCreateCommand
import spring.webmvc.application.dto.result.FlightResult
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.domain.cache.FlightCache
import spring.webmvc.domain.model.entity.Flight
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.FlightRepository
import spring.webmvc.presentation.exception.EntityNotFoundException

@Component
class FlightStrategy(
    private val flightCache: FlightCache,
    private val flightRepository: FlightRepository,
    private val objectMapper: ObjectMapper,
) : ProductStrategy {
    private val logger = LoggerFactory.getLogger(FlightStrategy::class.java)

    override fun supports(category: Category) = category == Category.FLIGHT

    override fun findByProductId(productId: Long): ProductResult {
        val cache = flightCache.get(productId)
            ?.let { value ->
                runCatching { objectMapper.readValue(value, FlightResult::class.java) }
                    .onFailure {
                        logger.warn("Failed to deserialize cache for productId={}: {}", productId, it.message)
                    }
                    .getOrNull()
            }

        if (cache != null) {
            return cache
        }

        val flightResult = (flightRepository.findByProductId(productId)
            ?.let { FlightResult(flight = it) }
            ?: throw EntityNotFoundException(kClass = Flight::class, id = productId))

        runCatching { objectMapper.writeValueAsString(flightResult) }
            .onSuccess { value -> flightCache.set(id = productId, value = value) }
            .onFailure { logger.warn("Failed to serialize cache for productId={}: {}", productId, it.message) }

        return flightResult
    }

    override fun createProduct(productCreateCommand: ProductCreateCommand): ProductResult {
        val flightCreateCommand = productCreateCommand as FlightCreateCommand

        val flight = flightRepository.save(
            Flight.create(
                name = flightCreateCommand.name,
                description = flightCreateCommand.description,
                price = flightCreateCommand.price,
                quantity = flightCreateCommand.quantity,
                airline = flightCreateCommand.airline,
                flightNumber = flightCreateCommand.flightNumber,
                departureAirport = flightCreateCommand.departureAirport,
                arrivalAirport = flightCreateCommand.arrivalAirport,
                departureTime = flightCreateCommand.departureTime,
                arrivalTime = flightCreateCommand.arrivalTime,
            )
        )

        return FlightResult(flight)
    }
}