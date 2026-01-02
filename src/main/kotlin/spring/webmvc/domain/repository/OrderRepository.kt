package spring.webmvc.domain.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.enums.OrderStatus

interface OrderRepository {
    fun findAll(pageable: Pageable, user: User?, orderStatus: OrderStatus?): Page<Order>
    fun findById(id: Long): Order
    fun findByIdAndUser(id: Long, user: User): Order?
    fun save(order: Order): Order
}