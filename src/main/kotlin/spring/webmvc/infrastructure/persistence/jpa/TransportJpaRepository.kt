package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.Transport

interface TransportJpaRepository : JpaRepository<Transport, Long> {
    fun findByProductId(productId: Long): Transport
}
