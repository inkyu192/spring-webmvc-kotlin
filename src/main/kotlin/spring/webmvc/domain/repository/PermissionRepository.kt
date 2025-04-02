package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Permission

interface PermissionRepository {
    fun findByIdOrNull(id: Long): Permission?
}