package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.model.entity.Permission
import spring.webmvc.domain.model.entity.Role
import spring.webmvc.domain.model.entity.RolePermission
import spring.webmvc.domain.repository.PermissionRepository
import spring.webmvc.domain.repository.RoleRepository
import spring.webmvc.presentation.dto.request.RoleSaveRequest
import spring.webmvc.presentation.dto.response.RoleResponse
import spring.webmvc.presentation.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class RoleService(
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository
) {
    @Transactional
    fun saveRole(roleSaveRequest: RoleSaveRequest): RoleResponse {
        val rolePermissions = roleSaveRequest.permissionIds.map {
            val permission = permissionRepository.findByIdOrNull(it)
                ?: throw EntityNotFoundException(Permission::class.java, it)

            RolePermission.create(permission)
        }

        val role = roleRepository.save(
            Role.create(
                name = roleSaveRequest.name,
                rolePermission = rolePermissions,
            )
        )

        return RoleResponse(role, role.rolePermissions.map { it.permission })
    }
}