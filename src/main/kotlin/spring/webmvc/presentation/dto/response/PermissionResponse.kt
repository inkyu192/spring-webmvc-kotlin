package spring.webmvc.presentation.dto.response

import spring.webmvc.domain.model.entity.Permission

data class PermissionResponse(
    val id: Long,
    val name: String
) {
    constructor(permission: Permission) : this(
        id = checkNotNull(permission.id),
        name = permission.name
    )
}