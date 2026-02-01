package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Menu
import spring.webmvc.domain.repository.MenuRepository
import spring.webmvc.infrastructure.persistence.jpa.MenuJpaRepository
import spring.webmvc.infrastructure.persistence.jpa.MenuQuerydslRepository

@Component
class MenuRepositoryAdapter(
    private val jpaRepository: MenuJpaRepository,
    private val querydslRepository: MenuQuerydslRepository,
) : MenuRepository {
    override fun findAll(): List<Menu> = jpaRepository.findAll()

    override fun findAllById(ids: List<Long>): List<Menu> = jpaRepository.findAllById(ids)

    override fun findAllByPermissions(permissions: Iterable<String>) =
        querydslRepository.findAllByPermissions(permissions)

    override fun saveAll(menus: Iterable<Menu>): List<Menu> = jpaRepository.saveAll(menus)
}