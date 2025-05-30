package spring.webmvc.application.dto.command

import spring.webmvc.presentation.dto.request.OrderProductCreateRequest

data class OrderProductCreateCommand(
    val productId: Long,
    val quantity: Long,
) {
    constructor(orderProductCreateRequest: OrderProductCreateRequest) : this(
        productId = orderProductCreateRequest.productId,
        quantity = orderProductCreateRequest.quantity,
    )
}
