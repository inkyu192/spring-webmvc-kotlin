package spring.webmvc.presentation.dto.response

import org.springframework.data.domain.Page
import spring.webmvc.application.dto.result.OrderDetailResult
import spring.webmvc.application.dto.result.OrderProductResult
import spring.webmvc.application.dto.result.OrderSummaryResult
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.infrastructure.persistence.dto.CursorPage
import java.time.Instant

data class OrderCursorPageResponse(
    val page: CursorPageResponse,
    val orders: List<OrderSummaryResponse>,
) {
    companion object {
        fun from(page: CursorPage<OrderSummaryResult>) = OrderCursorPageResponse(
            page = CursorPageResponse.from(page),
            orders = page.content.map { OrderSummaryResponse.from(result = it) },
        )
    }
}

data class OrderOffsetPageResponse(
    val page: OffsetPageResponse,
    val orders: List<OrderSummaryResponse>,
) {
    companion object {
        fun from(page: Page<OrderSummaryResult>) = OrderOffsetPageResponse(
            page = OffsetPageResponse.from(page),
            orders = page.content.map { OrderSummaryResponse.from(result = it) },
        )
    }
}

data class OrderSummaryResponse(
    val id: Long,
    val orderedAt: Instant,
    val status: OrderStatus,
) {
    companion object {
        fun from(result: OrderSummaryResult) = OrderSummaryResponse(
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
        fun from(result: OrderDetailResult) = OrderDetailResponse(
            id = result.id,
            orderedAt = result.orderedAt,
            status = result.status,
            products = result.products.map { OrderProductResponse.from(result = it) },
        )
    }
}

data class OrderProductResponse(
    val name: String,
    val price: Long,
    val quantity: Long,
) {
    companion object {
        fun from(result: OrderProductResult) = OrderProductResponse(
            name = result.name,
            price = result.price,
            quantity = result.quantity,
        )
    }
}