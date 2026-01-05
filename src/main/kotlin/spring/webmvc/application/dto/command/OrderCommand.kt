package spring.webmvc.application.dto.command

import spring.webmvc.domain.model.enums.OrderStatus

data class OrderCreateCommand(
    val userId: Long,
    val products: List<OrderProductCreateCommand>,
)

data class OrderProductCreateCommand(
    val id: Long,
    val quantity: Long,
)

data class OrderStatusUpdateCommand(
    val id: Long,
    val orderStatus: OrderStatus,
)