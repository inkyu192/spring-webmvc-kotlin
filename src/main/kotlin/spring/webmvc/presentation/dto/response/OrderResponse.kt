package spring.webmvc.presentation.dto.response

import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.enums.OrderStatus
import java.time.Instant

data class OrderResponse(
    val id: Long,
    val name: String,
    val orderedAt: Instant,
    val status: OrderStatus,
    val orderItems: List<OrderItemResponse>
) {
    constructor(order: Order) : this(
        id = checkNotNull(order.id),
        name = order.member.name,
        orderedAt = order.orderedAt,
        status = order.status,
        orderItems = order.orderItems.map { OrderItemResponse(it) }
    )
}