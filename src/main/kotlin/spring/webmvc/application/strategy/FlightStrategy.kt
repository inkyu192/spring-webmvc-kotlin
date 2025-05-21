package spring.webmvc.application.strategy

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.FlightCreateCommand
import spring.webmvc.application.dto.command.FlightUpdateCommand
import spring.webmvc.application.dto.command.ProductCreateCommand
import spring.webmvc.application.dto.command.ProductUpdateCommand
import spring.webmvc.application.dto.result.FlightResult
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.domain.cache.CacheKey
import spring.webmvc.domain.cache.KeyValueCache
import spring.webmvc.domain.model.entity.Flight
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.FlightRepository
import spring.webmvc.presentation.exception.EntityNotFoundException

@Component
class FlightStrategy(
    private val keyValueCache: KeyValueCache,
    private val flightRepository: FlightRepository,
    private val objectMapper: ObjectMapper,
) : ProductStrategy {
    private val logger = LoggerFactory.getLogger(FlightStrategy::class.java)

    override fun supports(category: Category) = category == Category.FLIGHT

    override fun findByProductId(productId: Long): ProductResult {
        val key = CacheKey.PRODUCT.generate(productId)

        val cache = keyValueCache.get(key)
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
            .onSuccess { value -> keyValueCache.set(key = key, value = value, timeout = CacheKey.PRODUCT.timeOut) }
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

        val key = CacheKey.PRODUCT_STOCK.generate(checkNotNull(flight.product.id))
        keyValueCache.set(key = key, value = flight.product.quantity.toString())

        return FlightResult(flight)
    }

    override fun updateProduct(productId: Long, productUpdateCommand: ProductUpdateCommand): ProductResult {
        val flightUpdateCommand = productUpdateCommand as FlightUpdateCommand

        val flight = flightRepository.findByProductId(productId)
            ?: throw EntityNotFoundException(kClass = Flight::class, id = productId)

        flight.update(
            name = flightUpdateCommand.name,
            description = flightUpdateCommand.description,
            price = flightUpdateCommand.price,
            quantity = flightUpdateCommand.quantity,
            airline = flightUpdateCommand.airline,
            flightNumber = flightUpdateCommand.flightNumber,
            departureAirport = flightUpdateCommand.departureAirport,
            arrivalAirport = flightUpdateCommand.arrivalAirport,
            departureTime = flightUpdateCommand.departureTime,
            arrivalTime = flightUpdateCommand.arrivalTime,
        )

        val key = CacheKey.PRODUCT_STOCK.generate(productId)
        keyValueCache.set(key = key, value = flight.product.quantity.toString())

        return FlightResult(flight)
    }

    override fun deleteProduct(productId: Long) {
        val flight = flightRepository.findByProductId(productId)
            ?: throw EntityNotFoundException(kClass = Flight::class, id = productId)

        val key = CacheKey.PRODUCT_STOCK.generate(productId)
        keyValueCache.delete(key = key)

        flightRepository.delete(flight)
    }
}