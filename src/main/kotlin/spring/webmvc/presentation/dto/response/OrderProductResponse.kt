package spring.webmvc.presentation.dto.response

import spring.webmvc.domain.model.entity.OrderProduct

data class OrderProductResponse(
    val productName: String,
    val orderPrice: Int,
    val count: Int,
) {
    constructor(orderProduct: OrderProduct) : this(
        productName = orderProduct.product.name,
        orderPrice = orderProduct.orderPrice,
        count = orderProduct.count
    )
}

