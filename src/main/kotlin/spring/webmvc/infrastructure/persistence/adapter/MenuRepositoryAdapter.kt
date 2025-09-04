package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Menu
import spring.webmvc.domain.repository.MenuRepository
import spring.webmvc.infrastructure.persistence.jpa.MenuJpaRepository

@Component
class MenuRepositoryAdapter(
    private val jpaRepository: MenuJpaRepository,
) : MenuRepository {
    override fun findAllById(ids: List<Long>) = jpaRepository.findAllById(ids)

    override fun findByPermissions(permissions: Iterable<String>) = jpaRepository.findByPermissions(permissions)

    override fun saveAll(menus: Iterable<Menu>) = jpaRepository.saveAll(menus)
}