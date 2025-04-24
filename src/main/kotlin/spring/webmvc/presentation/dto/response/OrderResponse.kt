package spring.webmvc.presentation.dto.response

import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.enums.OrderStatus
import java.time.Instant

data class OrderResponse(
    val id: Long,
    val name: String,
    val orderedAt: Instant,
    val status: OrderStatus,
    val orderProducts: List<OrderProductResponse>
) {
    constructor(order: Order) : this(
        id = checkNotNull(order.id),
        name = order.member.name,
        orderedAt = order.orderedAt,
        status = order.status,
        orderProducts = order.orderProducts.map { OrderProductResponse(it) }
    )
}