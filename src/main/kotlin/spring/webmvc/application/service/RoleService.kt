package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.model.entity.Permission
import spring.webmvc.domain.model.entity.Role
import spring.webmvc.domain.repository.PermissionRepository
import spring.webmvc.domain.repository.RoleRepository
import spring.webmvc.presentation.dto.request.RoleSaveRequest
import spring.webmvc.presentation.dto.response.RoleResponse
import spring.webmvc.presentation.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class RoleService(
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository,
) {
    @Transactional
    fun saveRole(roleSaveRequest: RoleSaveRequest): RoleResponse {
        val role = Role.create(roleSaveRequest.name)

        val permissionMap = permissionRepository.findAllById(roleSaveRequest.permissionIds).associateBy { it.id }
        roleSaveRequest.permissionIds.forEach {
            val permission = permissionMap[it] ?: throw EntityNotFoundException(clazz = Permission::class.java, id = it)
            role.addPermission(permission)
        }

        roleRepository.save(role)

        return RoleResponse(role = role, permissions = role.rolePermissions.map { it.permission })
    }
}