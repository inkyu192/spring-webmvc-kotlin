package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Min

data class OrderProductCreateRequest(
    val id: Long,
    @field:Min(1)
    val quantity: Long,
)
