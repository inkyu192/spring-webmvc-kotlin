package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.repository.AccommodationRepository
import spring.webmvc.infrastructure.persistence.jpa.AccommodationJpaRepository

@Component
class AccommodationRepositoryAdapter(
    private val jpaRepository: AccommodationJpaRepository,
) : AccommodationRepository {
    override fun findByProductId(productId: Long) = jpaRepository.findByProductId(productId)

    override fun save(accommodation: Accommodation): Accommodation = jpaRepository.save(accommodation)

    override fun delete(accommodation: Accommodation) {
        jpaRepository.delete(accommodation)
    }
}