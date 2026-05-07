package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import spring.webmvc.domain.model.entity.Order

interface OrderJpaRepository : JpaRepository<Order, Long> {
    fun findByIdAndUserId(id: Long, userId: Long): Order?

    @Query("SELECT MAX(o.orderNumber) FROM Order o WHERE o.orderNumber LIKE :date%")
    fun findMaxOrderNumberByDate(date: String): String?
}