package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Size

data class OrderCreateRequest(
    @field:Size(min = 1)
    val products: List<OrderProductCreateRequest> = emptyList(),
)
