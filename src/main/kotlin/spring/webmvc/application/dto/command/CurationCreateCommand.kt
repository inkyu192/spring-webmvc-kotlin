package spring.webmvc.application.dto.command

import spring.webmvc.presentation.dto.request.CurationCreateRequest

data class CurationCreateCommand(
    val title: String,
    val isExposed: Boolean,
    val sortOrder: Long,
    val products: List<CurationProductCreateCommand>,
) {
    constructor(curationCreateRequest: CurationCreateRequest) : this(
        title = curationCreateRequest.title,
        isExposed = curationCreateRequest.isExposed,
        sortOrder = curationCreateRequest.sortOrder,
        products = curationCreateRequest.products.map { CurationProductCreateCommand(curationProductCreateRequest = it) },
    )
}