package spring.webmvc.application.dto.command

import spring.webmvc.presentation.dto.request.CurationProductCreateRequest

data class CurationProductCreateCommand(
    val productId: Long,
    val sortOrder: Long,
) {
    constructor(curationProductCreateRequest: CurationProductCreateRequest) : this(
        productId = curationProductCreateRequest.id,
        sortOrder = curationProductCreateRequest.sortOrder,
    )
}
