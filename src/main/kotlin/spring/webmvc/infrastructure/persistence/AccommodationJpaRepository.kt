package spring.webmvc.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.Accommodation

interface AccommodationJpaRepository : JpaRepository<Accommodation, Long> {
    fun findByProductId(productId: Long): Accommodation?
}