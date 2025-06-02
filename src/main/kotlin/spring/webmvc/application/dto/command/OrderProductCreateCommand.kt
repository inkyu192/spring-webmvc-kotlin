package spring.webmvc.application.dto.command

import spring.webmvc.presentation.dto.request.OrderProductCreateRequest

data class OrderProductCreateCommand(
    val id: Long,
    val quantity: Long,
) {
    constructor(orderProductCreateRequest: OrderProductCreateRequest) : this(
        id = orderProductCreateRequest.id,
        quantity = orderProductCreateRequest.quantity,
    )
}
