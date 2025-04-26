package spring.webmvc.presentation.dto.response

import spring.webmvc.domain.model.entity.OrderProduct

data class OrderProductResponse(
    val productName: String,
    val orderPrice: Int,
    val quantity: Int,
) {
    constructor(orderProduct: OrderProduct) : this(
        productName = orderProduct.product.name,
        orderPrice = orderProduct.orderPrice,
        quantity = orderProduct.quantity
    )
}

