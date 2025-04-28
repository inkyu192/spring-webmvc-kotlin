package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.model.entity.Role
import spring.webmvc.domain.repository.RoleRepository
import spring.webmvc.presentation.dto.request.RoleSaveRequest
import spring.webmvc.presentation.dto.response.RoleResponse

@Service
@Transactional(readOnly = true)
class RoleService(
    private val roleRepository: RoleRepository,
    private val permissionService: PermissionService,
) {
    @Transactional
    fun saveRole(roleSaveRequest: RoleSaveRequest): RoleResponse {
        val role = Role.create(roleSaveRequest.name)

        permissionService.addPermission(
            permissionIds = roleSaveRequest.permissionIds,
            consumer = role::addPermission,
        )

        roleRepository.save(role)

        return RoleResponse(role = role, permissions = role.rolePermissions.map { it.permission })
    }
}