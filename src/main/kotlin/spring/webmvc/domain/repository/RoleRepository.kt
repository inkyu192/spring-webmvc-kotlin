package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Role

interface RoleRepository {
    fun save(role: Role): Role
    fun saveAll(roles: Iterable<Role>): List<Role>
    fun findAllById(ids: Iterable<Long>): List<Role>
}