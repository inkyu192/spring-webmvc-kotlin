package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Size
import spring.webmvc.application.dto.command.CurationCreateCommand

data class CurationCreateRequest(
    val title: String,
    val isExposed: Boolean,
    val sortOrder: Long,
    @field:Size(min = 1)
    val products: List<CurationProductCreateRequest> = emptyList(),
) {
    fun toCommand() = CurationCreateCommand(
        title = title,
        isExposed = isExposed,
        sortOrder = sortOrder,
        products = products.map { it.toCommand() }.toList()
    )
}