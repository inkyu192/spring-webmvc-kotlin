package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Size

data class RoleCreateRequest(
    val name: String,
    @field:Size(min = 1)
    val permissionIds: List<Long> = emptyList(),
)
