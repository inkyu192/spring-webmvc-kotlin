package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Size
import spring.webmvc.application.dto.command.CurationCreateCommand
import spring.webmvc.application.dto.command.CurationProductCreateCommand

data class CurationCreateRequest(
    val title: String,
    val category: String,
    val isExposed: Boolean,
    val sortOrder: Long,
    @field:Size(min = 1)
    val products: List<CurationProductCreateRequest> = emptyList(),
) {
    fun toCommand() = CurationCreateCommand(
        title = title,
        category = spring.webmvc.domain.model.enums.CurationCategory.valueOf(category),
        isExposed = isExposed,
        sortOrder = sortOrder,
        products = products.map { it.toCommand() }.toList()
    )
}

data class CurationProductCreateRequest(
    val productId: Long,
    val sortOrder: Long,
) {
    fun toCommand() = CurationProductCreateCommand(
        productId = productId,
        sortOrder = sortOrder,
    )
}