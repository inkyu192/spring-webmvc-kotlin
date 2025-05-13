package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.repository.FlightRepository
import spring.webmvc.presentation.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class FlightService(
    private val flightRepository: FlightRepository,
) {
    @Transactional
    fun deleteFlight(id: Long) {
        val flight = flightRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(kClass = FlightRepository::class, id = id)

        flightRepository.delete(flight)
    }
}
