package spring.webmvc.application.strategy

import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.FlightCreateCommand
import spring.webmvc.application.dto.command.FlightUpdateCommand
import spring.webmvc.application.dto.command.ProductCreateCommand
import spring.webmvc.application.dto.command.ProductUpdateCommand
import spring.webmvc.application.dto.result.FlightResult
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.domain.cache.CacheKey
import spring.webmvc.domain.cache.ValueCache
import spring.webmvc.domain.model.entity.Flight
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.FlightRepository
import spring.webmvc.presentation.exception.EntityNotFoundException

@Component
class FlightStrategy(
    private val valueCache: ValueCache,
    private val flightRepository: FlightRepository,
) : ProductStrategy {
    override fun category() = Category.FLIGHT

    override fun findByProductId(productId: Long): ProductResult {
        val key = CacheKey.FLIGHT.generate(productId)
        val cache = valueCache.get(key = key, clazz = FlightResult::class.java)

        if (cache != null) {
            return cache
        }

        val flightResult = (flightRepository.findByProductId(productId)
            ?.let { FlightResult(flight = it) }
            ?: throw EntityNotFoundException(kClass = Flight::class, id = productId))

        valueCache.set(key = key, value = flightResult, timeout = CacheKey.FLIGHT.timeOut)

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

        return FlightResult(flight)
    }

    override fun deleteProduct(productId: Long) {
        val flight = flightRepository.findByProductId(productId)
            ?: throw EntityNotFoundException(kClass = Flight::class, id = productId)

        flightRepository.delete(flight)

        val key = CacheKey.FLIGHT.generate(productId)
        valueCache.delete(key = key)
    }
}