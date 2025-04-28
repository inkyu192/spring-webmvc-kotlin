package spring.webmvc.presentation.dto.response

import spring.webmvc.domain.model.entity.RolePermission

data class PermissionResponse(
    val id: Long,
    val name: String
) {
    constructor(rolePermission: RolePermission) : this(
        id = checkNotNull(rolePermission.permission.id),
        name = rolePermission.permission.name
    )
}