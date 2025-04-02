package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import spring.webmvc.domain.model.enums.Category


data class ItemSaveRequest(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val description: String,
    @field:Min(100)
    val price: Int,
    @field:Max(9999)
    val quantity: Int,
    val category: Category,
)
