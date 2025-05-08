package spring.webmvc.application.dto.command

import spring.webmvc.presentation.dto.request.OrderCreateRequest

data class OrderCreateCommand(
    val products: List<OrderProductCreateCommand>
) {
    constructor(orderCreateRequest: OrderCreateRequest) : this(
        products = orderCreateRequest.products
            .map { OrderProductCreateCommand(orderProductCreateRequest = it) }
            .toList()
    )
}
