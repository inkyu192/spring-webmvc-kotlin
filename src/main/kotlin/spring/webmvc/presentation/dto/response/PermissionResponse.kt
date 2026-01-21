package spring.webmvc.presentation.dto.response

import spring.webmvc.domain.model.entity.RolePermission

data class PermissionResponse(
    val id: Long,
    val name: String,
) {
    companion object {
        fun of(rolePermission: RolePermission): PermissionResponse {
            return PermissionResponse(
                id = checkNotNull(rolePermission.permission.id),
                name = rolePermission.permission.name
            )
        }
    }
}