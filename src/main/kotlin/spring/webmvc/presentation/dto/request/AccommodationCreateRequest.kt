package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.time.Instant

data class AccommodationCreateRequest(
    val name: String,
    val description: String,
    @field:Min(100)
    val price: Int,
    @field:Max(9999)
    val quantity: Int,
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
)