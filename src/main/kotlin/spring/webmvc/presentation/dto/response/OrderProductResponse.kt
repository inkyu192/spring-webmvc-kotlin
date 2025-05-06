package spring.webmvc.presentation.dto.response

import spring.webmvc.domain.model.entity.OrderProduct

data class OrderProductResponse(
    val name: String,
    val price: Int,
    val quantity: Int,
) {
    constructor(orderProduct: OrderProduct) : this(
        name = orderProduct.product.name,
        price = orderProduct.orderPrice,
        quantity = orderProduct.quantity
    )
}

