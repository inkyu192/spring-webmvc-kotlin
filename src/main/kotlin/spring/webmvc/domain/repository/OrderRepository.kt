package spring.webmvc.domain.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.enums.OrderStatus

interface OrderRepository {
    fun findAll(pageable: Pageable, memberId: Long?, orderStatus: OrderStatus?): Page<Order>
    fun findByIdOrNull(id: Long): Order?
    fun findByIdAndMemberId(id: Long, memberId: Long): Order?
    fun save(order: Order): Order
}