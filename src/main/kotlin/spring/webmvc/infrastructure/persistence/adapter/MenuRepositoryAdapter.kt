package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Menu
import spring.webmvc.domain.repository.MenuRepository
import spring.webmvc.infrastructure.persistence.jpa.MenuJpaRepository

@Component
class MenuRepositoryAdapter(
    private val jpaRepository: MenuJpaRepository,
) : MenuRepository {
    override fun findByIdOrNull(id: Long) = jpaRepository.findByIdOrNull(id)

    override fun findAllByPermissionNameIn(permissions: Iterable<String>) =
        jpaRepository.findAllByPermissionNameIn(permissions)

    override fun save(menu: Menu) = jpaRepository.save(menu)

    override fun saveAll(menus: Iterable<Menu>) = jpaRepository.saveAll(menus)
}