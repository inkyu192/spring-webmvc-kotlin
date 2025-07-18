package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Min
import spring.webmvc.application.dto.command.OrderProductCreateCommand

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
