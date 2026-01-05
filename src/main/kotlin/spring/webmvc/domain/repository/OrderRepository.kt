package spring.webmvc.domain.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.infrastructure.persistence.dto.CursorPage
import java.time.Instant

interface OrderRepository {
    fun findAllWithOffsetPage(
        pageable: Pageable,
        userId: Long?,
        orderStatus: OrderStatus?,
        orderedFrom: Instant?,
        orderedTo: Instant?,
    ): Page<Order>

    fun findAllWithCursorPage(
        cursorId: Long?,
        userId: Long?,
        orderStatus: OrderStatus?,
        orderedFrom: Instant?,
        orderedTo: Instant?,
    ): CursorPage<Order>

    fun findById(id: Long): Order
    fun findByIdAndUserId(id: Long, userId: Long): Order?
    fun save(order: Order): Order
}