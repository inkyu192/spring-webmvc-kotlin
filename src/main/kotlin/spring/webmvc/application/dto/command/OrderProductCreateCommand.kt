package spring.webmvc.application.dto.command

data class OrderProductCreateCommand(
    val id: Long,
    val quantity: Long,
)
