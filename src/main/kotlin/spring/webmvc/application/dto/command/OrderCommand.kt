package spring.webmvc.application.dto.command

data class OrderCreateCommand(
    val userId: Long,
    val products: List<OrderProductCreateCommand>,
)

data class OrderProductCreateCommand(
    val id: Long,
    val quantity: Long,
)

data class OrderCancelCommand(
    val userId: Long,
    val id: Long,
)