package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Menu

interface MenuRepository {
    fun findByIdOrNull(id: Long): Menu?
    fun findAllByPermissionNameIn(permissions: Iterable<String>): List<Menu>
    fun save(menu: Menu): Menu
    fun saveAll(menus: Iterable<Menu>): List<Menu>
}