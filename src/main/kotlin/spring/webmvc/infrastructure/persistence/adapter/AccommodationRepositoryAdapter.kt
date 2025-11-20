package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.repository.AccommodationRepository
import spring.webmvc.infrastructure.extensions.findByIdOrThrow
import spring.webmvc.infrastructure.persistence.jpa.AccommodationJpaRepository

@Component
class AccommodationRepositoryAdapter(
    private val jpaRepository: AccommodationJpaRepository,
) : AccommodationRepository {
    override fun findById(id: Long): Accommodation = jpaRepository.findByIdOrThrow(id)

    override fun save(accommodation: Accommodation) = jpaRepository.save(accommodation)

    override fun delete(accommodation: Accommodation) {
        jpaRepository.delete(accommodation)
    }
}