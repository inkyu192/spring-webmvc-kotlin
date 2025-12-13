package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Role

interface RoleRepository {
    fun findAllById(ids: Iterable<Long>): List<Role>
}