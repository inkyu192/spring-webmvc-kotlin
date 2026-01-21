package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.OrderDetailResult
import spring.webmvc.application.dto.result.OrderProductResult
import spring.webmvc.application.dto.result.OrderSummaryResult
import spring.webmvc.domain.model.enums.OrderStatus
import java.time.Instant

data class OrderSummaryResponse(
    val id: Long,
    val orderedAt: Instant,
    val status: OrderStatus,
) {
    companion object {
        fun of(result: OrderSummaryResult) = OrderSummaryResponse(
            id = result.id,
            orderedAt = result.orderedAt,
            status = result.status,
        )
    }
}

data class OrderDetailResponse(
    val id: Long,
    val orderedAt: Instant,
    val status: OrderStatus,
    val products: List<OrderProductResponse>,
) {
    companion object {
        fun of(result: OrderDetailResult) = OrderDetailResponse(
            id = result.id,
            orderedAt = result.orderedAt,
            status = result.status,
            products = result.products.map { OrderProductResponse.of(result = it) },
        )
    }
}

data class OrderProductResponse(
    val name: String,
    val price: Long,
    val quantity: Long,
) {
    companion object {
        fun of(result: OrderProductResult) = OrderProductResponse(
            name = result.name,
            price = result.price,
            quantity = result.quantity,
        )
    }
}