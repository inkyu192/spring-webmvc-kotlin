package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Permission

interface PermissionRepository {
    fun findAllById(ids: Iterable<Long>): List<Permission>
    fun save(permission: Permission): Permission
}