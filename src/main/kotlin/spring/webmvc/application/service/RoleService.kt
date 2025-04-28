package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.model.entity.Role
import spring.webmvc.domain.repository.RoleRepository

@Service
@Transactional(readOnly = true)
class RoleService(
    private val roleRepository: RoleRepository,
    private val permissionService: PermissionService,
) {
    @Transactional
    fun createRole(name: String, permissionIds: List<Long>): Role {
        val role = Role.create(name)

        permissionService.addPermission(permissionIds = permissionIds, consumer = role::addPermission)

        return roleRepository.save(role)
    }
}