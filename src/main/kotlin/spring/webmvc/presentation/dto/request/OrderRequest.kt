package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import spring.webmvc.application.dto.command.OrderCreateCommand
import spring.webmvc.application.dto.command.OrderProductCreateCommand
import spring.webmvc.application.dto.command.OrderStatusUpdateCommand
import spring.webmvc.domain.model.enums.OrderStatus

data class OrderCreateRequest(
    @field:Size(min = 1)
    val products: List<OrderProductCreateRequest> = emptyList(),
) {
    fun toCommand(userId: Long) = OrderCreateCommand(
        userId = userId,
        products = products.map { it.toCommand() }.toList(),
    )
}

data class OrderProductCreateRequest(
    val id: Long,
    @field:Min(1)
    val quantity: Long,
) {
    fun toCommand() = OrderProductCreateCommand(
        id = id,
        quantity = quantity,
    )
}

data class OrderStatusUpdateRequest(
    val orderStatus: OrderStatus,
) {
    fun toCommand(id: Long) = OrderStatusUpdateCommand(
        id = id,
        orderStatus = orderStatus,
    )
}