package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Flight
import spring.webmvc.domain.repository.FlightRepository
import spring.webmvc.infrastructure.persistence.FlightJpaRepository

@Component
class FlightRepositoryAdapter(
    private val jpaRepository: FlightJpaRepository,
) : FlightRepository {
    override fun findByIdOrNull(id: Long) = jpaRepository.findByIdOrNull(id)

    override fun findByProductId(productId: Long) = jpaRepository.findByProductId(productId)

    override fun save(flight: Flight) = jpaRepository.save(flight)

    override fun delete(flight: Flight) {
        jpaRepository.delete(flight)
    }
}