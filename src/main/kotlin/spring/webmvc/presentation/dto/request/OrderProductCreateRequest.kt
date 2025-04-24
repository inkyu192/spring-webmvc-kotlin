package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Min

data class OrderProductCreateRequest(
    val productId: Long,
    @field:Min(1)
    val count: Int,
)
