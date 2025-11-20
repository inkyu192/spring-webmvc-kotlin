package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Flight
import spring.webmvc.domain.repository.FlightRepository
import spring.webmvc.infrastructure.extensions.findByIdOrThrow
import spring.webmvc.infrastructure.persistence.jpa.FlightJpaRepository

@Component
class FlightRepositoryAdapter(
    private val jpaRepository: FlightJpaRepository,
) : FlightRepository {
    override fun findById(id: Long): Flight = jpaRepository.findByIdOrThrow(id)

    override fun save(flight: Flight) = jpaRepository.save(flight)

    override fun delete(flight: Flight) {
        jpaRepository.delete(flight)
    }
}