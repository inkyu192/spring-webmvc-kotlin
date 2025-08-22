package spring.webmvc.application.strategy

import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.FlightCreateCommand
import spring.webmvc.application.dto.command.FlightUpdateCommand
import spring.webmvc.application.dto.command.ProductCreateCommand
import spring.webmvc.application.dto.command.ProductUpdateCommand
import spring.webmvc.application.dto.result.FlightResult
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.domain.model.entity.Flight
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.cache.FlightCacheRepository
import spring.webmvc.domain.repository.FlightRepository
import spring.webmvc.presentation.exception.EntityNotFoundException

@Component
class FlightStrategy(
    private val flightCacheRepository: FlightCacheRepository,
    private val flightRepository: FlightRepository,
) : ProductStrategy {
    override fun category() = Category.FLIGHT

    override fun findByProductId(productId: Long): ProductResult {
        val cache = flightCacheRepository.getFlight(productId)

        if (cache != null) {
            return cache
        }

        val flightResult = (flightRepository.findByProductId(productId)
            ?.let { FlightResult(flight = it) }
            ?: throw EntityNotFoundException(kClass = Flight::class, id = productId))

        flightCacheRepository.setFlight(productId, flightResult)

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

        flightCacheRepository.deleteFlight(productId)
    }
}