package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Size

data class CurationCreateRequest(
    val title: String,
    val isExposed: Boolean,
    val sortOrder: Long,
    @field:Size(min = 1)
    val products: List<CurationProductCreateRequest> = emptyList(),
)