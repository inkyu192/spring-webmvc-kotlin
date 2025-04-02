package spring.webmvc.presentation.dto.response

import spring.webmvc.domain.model.entity.OrderItem

data class OrderItemResponse(
    val itemName: String,
    val orderPrice: Int,
    val count: Int,
) {
    constructor(orderItem: OrderItem) : this(
        itemName = orderItem.item.name,
        orderPrice = orderItem.orderPrice,
        count = orderItem.count
    )
}

