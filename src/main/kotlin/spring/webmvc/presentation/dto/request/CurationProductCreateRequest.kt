package spring.webmvc.presentation.dto.request

import spring.webmvc.application.dto.command.CurationProductCreateCommand

data class CurationProductCreateRequest(
    val productId: Long,
) {
    fun toCommand() = CurationProductCreateCommand(
        productId = productId,
    )
}