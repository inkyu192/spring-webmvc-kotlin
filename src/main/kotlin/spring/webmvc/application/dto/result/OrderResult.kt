package spring.webmvc.application.dto.result

import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.entity.OrderProduct
import spring.webmvc.domain.model.enums.OrderStatus
import java.time.Instant

data class OrderResult(
    val id: Long,
    val orderedAt: Instant,
    val status: OrderStatus,
    val products: List<OrderProductResult>,
) {
    companion object {
        fun from(order: Order) = OrderResult(
            id = checkNotNull(order.id),
            orderedAt = order.orderedAt,
            status = order.status,
            products = order.orderProducts.map { OrderProductResult.from(it) },
        )
    }
}

data class OrderProductResult(
    val name: String,
    val price: Long,
    val quantity: Long,
) {
    companion object {
        fun from(orderProduct: OrderProduct) = OrderProductResult(
            name = orderProduct.product.name,
            price = orderProduct.orderPrice,
            quantity = orderProduct.quantity,
        )
    }
}