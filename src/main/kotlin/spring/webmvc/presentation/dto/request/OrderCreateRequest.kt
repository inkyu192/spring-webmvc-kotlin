package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Size

data class OrderCreateRequest(
    val memberId: Long,
    val city: String,
    val street: String,
    val zipcode: String,
    @field:Size(min = 1)
    val orderProducts: List<OrderProductCreateRequest> = emptyList(),
)
