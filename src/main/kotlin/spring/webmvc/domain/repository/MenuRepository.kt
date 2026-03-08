package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Menu

interface MenuRepository {
    fun findAllWithRecursiveByPermissions(permissions: Iterable<String>): List<Menu>
}
