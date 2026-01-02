package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.entity.User

interface OrderJpaRepository : JpaRepository<Order, Long> {
    fun findByIdAndUser(id: Long, user: User): Order?
}