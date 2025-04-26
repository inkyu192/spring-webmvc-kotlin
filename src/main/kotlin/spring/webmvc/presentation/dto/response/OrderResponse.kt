package spring.webmvc.presentation.dto.response

import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.enums.OrderStatus
import java.time.Instant

data class OrderResponse(
    val id: Long,
    val orderedAt: Instant,
    val status: OrderStatus,
    val products: List<OrderProductResponse>
) {
    constructor(order: Order) : this(
        id = checkNotNull(order.id),
        orderedAt = order.orderedAt,
        status = order.status,
        products = order.orderProducts.map { OrderProductResponse(orderProduct = it) }
    )
}