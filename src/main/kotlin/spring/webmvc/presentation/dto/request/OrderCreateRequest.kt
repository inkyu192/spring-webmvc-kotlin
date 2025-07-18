package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Size
import spring.webmvc.application.dto.command.OrderCreateCommand

data class OrderCreateRequest(
    @field:Size(min = 1)
    val products: List<OrderProductCreateRequest> = emptyList(),
) {
    fun toCommand() = OrderCreateCommand(
        products = products.map { it.toCommand() }
            .toList()
    )
}
