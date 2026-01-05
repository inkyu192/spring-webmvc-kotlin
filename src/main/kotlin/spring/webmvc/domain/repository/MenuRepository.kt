package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Menu

interface MenuRepository {
    fun findAllById(ids: List<Long>): List<Menu>
    fun findAllByPermissions(permissions: Iterable<String>): List<Menu>
    fun saveAll(menus: Iterable<Menu>): List<Menu>
}