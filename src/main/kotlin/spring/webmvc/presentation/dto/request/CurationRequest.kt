package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Size
import spring.webmvc.application.dto.command.CurationCreateCommand
import spring.webmvc.application.dto.command.CurationProductCreateCommand
import spring.webmvc.domain.model.enums.CurationCategory

data class CurationCreateRequest(
    val title: String,
    val category: CurationCategory,
    val isExposed: Boolean,
    val sortOrder: Long,
    @field:Size(min = 1)
    val products: List<CurationProductCreateRequest> = emptyList(),
) {
    fun toCommand() = CurationCreateCommand(
        title = title,
        category = category,
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
