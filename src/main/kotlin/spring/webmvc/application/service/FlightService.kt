package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.model.entity.Flight
import spring.webmvc.domain.repository.FlightRepository
import spring.webmvc.presentation.dto.request.FlightCreateRequest
import spring.webmvc.presentation.dto.request.FlightUpdateRequest
import spring.webmvc.presentation.dto.response.FlightResponse
import spring.webmvc.presentation.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class FlightService(
    private val flightRepository: FlightRepository,
) {
    fun findFlight(id: Long): FlightResponse {
        val flight = flightRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(clazz = FlightRepository::class.java, id = id)

        return FlightResponse(flight = flight)
    }

    @Transactional
    fun createFlight(flightCreateRequest: FlightCreateRequest): FlightResponse {
        val flight = flightRepository.save(
            Flight.create(
                name = flightCreateRequest.name,
                description = flightCreateRequest.description,
                price = flightCreateRequest.price,
                quantity = flightCreateRequest.quantity,
                flightNumber = flightCreateRequest.flightNumber,
                airline = flightCreateRequest.airline,
                departureAirport = flightCreateRequest.departureAirport,
                arrivalAirport = flightCreateRequest.arrivalAirport,
                departureTime = flightCreateRequest.departureTime,
                arrivalTime = flightCreateRequest.arrivalTime,
            )
        )

        return FlightResponse(flight = flight)
    }

    @Transactional
    fun updateFlight(id: Long, flightUpdateRequest: FlightUpdateRequest): FlightResponse {
        val flight = flightRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(clazz = FlightRepository::class.java, id = id)

        flight.update(
            name = flightUpdateRequest.name,
            description = flightUpdateRequest.description,
            price = flightUpdateRequest.price,
            quantity = flightUpdateRequest.quantity,
            airline = flightUpdateRequest.airline,
            flightNumber = flightUpdateRequest.flightNumber,
            departureAirport = flightUpdateRequest.departureAirport,
            arrivalAirport = flightUpdateRequest.arrivalAirport,
            departureTime = flightUpdateRequest.departureTime,
            arrivalTime = flightUpdateRequest.arrivalTime,
        )

        return FlightResponse(flight = flight)
    }

    @Transactional
    fun deleteFlight(id: Long) {
        val flight = flightRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(clazz = FlightRepository::class.java, id = id)

        flightRepository.delete(flight)
    }
}