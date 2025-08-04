package spring.webmvc.presentation.dto.request

import spring.webmvc.application.dto.command.CurationProductCreateCommand

data class CurationProductCreateRequest(
    val productId: Long,
    val sortOrder: Long,
) {
    fun toCommand() = CurationProductCreateCommand(
        productId = productId,
        sortOrder = sortOrder,
    )
}