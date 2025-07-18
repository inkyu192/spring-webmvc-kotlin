package spring.webmvc.application.dto.command

data class OrderCreateCommand(
    val products: List<OrderProductCreateCommand>
)
