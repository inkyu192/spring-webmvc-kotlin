package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Permission
import spring.webmvc.domain.repository.PermissionRepository
import spring.webmvc.infrastructure.persistence.PermissionJpaRepository

@Component
class PermissionRepositoryAdapter(
    private val jpaRepository: PermissionJpaRepository
) : PermissionRepository {
    override fun findAllById(ids: Iterable<Long>): List<Permission> = jpaRepository.findAllById(ids)
    override fun save(permission: Permission) = jpaRepository.save(permission)
}