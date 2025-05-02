package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.model.entity.Flight
import spring.webmvc.domain.repository.FlightRepository
import spring.webmvc.presentation.exception.EntityNotFoundException
import java.time.Instant

@Service
@Transactional(readOnly = true)
class FlightService(
    private val flightRepository: FlightRepository,
) {
    fun findFlight(id: Long) = flightRepository.findByIdOrNull(id)
        ?: throw EntityNotFoundException(kClass = FlightRepository::class, id = id)

    @Transactional
    fun createFlight(
        name: String,
        description: String,
        price: Int,
        quantity: Int,
        airline: String,
        flightNumber: String,
        departureAirport: String,
        arrivalAirport: String,
        departureTime: Instant,
        arrivalTime: Instant,
    ) = flightRepository.save(
        Flight.create(
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            flightNumber = flightNumber,
            airline = airline,
            departureAirport = departureAirport,
            arrivalAirport = arrivalAirport,
            departureTime = departureTime,
            arrivalTime = arrivalTime,
        )
    )

    @Transactional
    fun updateFlight(
        id: Long,
        name: String,
        description: String,
        price: Int,
        quantity: Int,
        airline: String,
        flightNumber: String,
        departureAirport: String,
        arrivalAirport: String,
        departureTime: Instant,
        arrivalTime: Instant,
    ): Flight {
        val flight = flightRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(kClass = FlightRepository::class, id = id)

        flight.update(
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            airline = airline,
            flightNumber = flightNumber,
            departureAirport = departureAirport,
            arrivalAirport = arrivalAirport,
            departureTime = departureTime,
            arrivalTime = arrivalTime,
        )

        return flight
    }

    @Transactional
    fun deleteFlight(id: Long) {
        val flight = flightRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(kClass = FlightRepository::class, id = id)

        flightRepository.delete(flight)
    }
}