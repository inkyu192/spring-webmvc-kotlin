package spring.webmvc.application.dto.result

import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.entity.OrderProduct
import spring.webmvc.domain.model.enums.OrderStatus
import java.time.Instant

data class OrderSummaryResult(
    val id: Long,
    val orderedAt: Instant,
    val status: OrderStatus,
) {
    companion object {
        fun of(order: Order) = OrderSummaryResult(
            id = checkNotNull(order.id),
            orderedAt = order.orderedAt,
            status = order.status,
        )
    }
}

data class OrderDetailResult(
    val id: Long,
    val orderedAt: Instant,
    val status: OrderStatus,
    val products: List<OrderProductResult>,
) {
    companion object {
        fun of(order: Order) = OrderDetailResult(
            id = checkNotNull(order.id),
            orderedAt = order.orderedAt,
            status = order.status,
            products = order.orderProducts.map { OrderProductResult.of(orderProduct = it) },
        )
    }
}

data class OrderProductResult(
    val name: String,
    val price: Long,
    val quantity: Long,
) {
    companion object {
        fun of(orderProduct: OrderProduct) = OrderProductResult(
            name = orderProduct.product.name,
            price = orderProduct.orderPrice,
            quantity = orderProduct.quantity,
        )
    }
}