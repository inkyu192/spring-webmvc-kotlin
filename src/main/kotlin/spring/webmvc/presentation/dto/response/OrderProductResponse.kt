package spring.webmvc.presentation.dto.response

import spring.webmvc.domain.model.entity.OrderProduct

data class OrderProductResponse(
    val name: String,
    val price: Long,
    val quantity: Long,
) {
    constructor(orderProduct: OrderProduct) : this(
        name = orderProduct.product.name,
        price = orderProduct.orderPrice,
        quantity = orderProduct.quantity
    )
}

