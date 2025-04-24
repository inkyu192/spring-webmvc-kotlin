package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.repository.AccommodationRepository
import spring.webmvc.infrastructure.persistence.AccommodationJpaRepository

@Component
class AccommodationRepositoryAdapter(
    private val jpaRepository: AccommodationJpaRepository,
): AccommodationRepository {
    override fun findByIdOrNull(id: Long) = jpaRepository.findByIdOrNull(id)

    override fun save(accommodation: Accommodation) = jpaRepository.save(accommodation)

    override fun delete(accommodation: Accommodation) {
        jpaRepository.delete(accommodation)
    }
}