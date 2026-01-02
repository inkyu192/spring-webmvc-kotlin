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

    override fun findByProductId(productId: Long): Accommodation =
        jpaRepository.findByProductId(productId)
            ?: throw NoSuchElementException("Accommodation not found with product id: $productId")

    override fun save(accommodation: Accommodation): Accommodation = jpaRepository.save(accommodation)

    override fun delete(accommodation: Accommodation) {
        jpaRepository.delete(accommodation)
    }
}