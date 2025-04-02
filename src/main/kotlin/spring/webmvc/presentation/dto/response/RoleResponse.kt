package spring.webmvc.presentation.dto.response

import spring.webmvc.domain.model.entity.Permission
import spring.webmvc.domain.model.entity.Role

data class RoleResponse(
    val id: Long,
    val name: String,
    val permissions: List<PermissionResponse>
) {
    constructor(role: Role, permissions: List<Permission>) : this(
        id = checkNotNull(role.id),
        name = role.name,
        permissions = permissions.map { PermissionResponse(it) }
    )
}