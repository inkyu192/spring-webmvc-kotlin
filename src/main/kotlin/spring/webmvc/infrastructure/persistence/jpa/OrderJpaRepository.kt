package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.Order

interface OrderJpaRepository : JpaRepository<Order, Long> {
    fun findByIdAndMemberId(id: Long, memberId: Long): Order?
}