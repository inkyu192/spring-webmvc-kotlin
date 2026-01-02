package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.OrderProductResult
import spring.webmvc.application.dto.result.OrderResult
import spring.webmvc.domain.model.enums.OrderStatus
import java.time.Instant

data class OrderResponse(
    val id: Long,
    val orderedAt: Instant,
    val status: OrderStatus,
    val products: List<OrderProductResponse>,
) {
    companion object {
        fun from(orderResult: OrderResult): OrderResponse {
            return OrderResponse(
                id = orderResult.id,
                orderedAt = orderResult.orderedAt,
                status = orderResult.status,
                products = orderResult.products.map { OrderProductResponse.from(it) }
            )
        }
    }
}

data class OrderProductResponse(
    val name: String,
    val price: Long,
    val quantity: Long,
) {
    companion object {
        fun from(orderProductResult: OrderProductResult): OrderProductResponse {
            return OrderProductResponse(
                name = orderProductResult.name,
                price = orderProductResult.price,
                quantity = orderProductResult.quantity
            )
        }
    }
}