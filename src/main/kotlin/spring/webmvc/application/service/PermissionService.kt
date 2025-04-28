package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.model.entity.Permission
import spring.webmvc.domain.repository.PermissionRepository
import spring.webmvc.presentation.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class PermissionService(
    private val permissionRepository: PermissionRepository,
) {
    fun addPermission(permissionIds: List<Long>, consumer: (Permission) -> Unit) {
        val permissionMap = permissionRepository.findAllById(permissionIds).associateBy { it.id }
        permissionIds.forEach {
            val permission = permissionMap[it] ?: throw EntityNotFoundException(clazz = Permission::class.java, id = it)
            consumer(permission)
        }
    }
}