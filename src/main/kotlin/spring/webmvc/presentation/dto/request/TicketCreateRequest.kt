package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.time.Instant

data class TicketCreateRequest(
    val name: String,
    val description: String,
    @field:Min(100)
    val price: Int,
    @field:Max(9999)
    val quantity: Int,
    val place: String,
    val performanceTime: Instant,
    val duration: String,
    val ageLimit: String,
)
