package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Menu
import spring.webmvc.domain.repository.MenuRepository
import spring.webmvc.infrastructure.persistence.jpa.MenuJpaRepository

@Component
class MenuRepositoryAdapter(
    private val jpaRepository: MenuJpaRepository,
) : MenuRepository {
    override fun findAllWithRecursiveByPermissions(permissions: Iterable<String>): List<Menu> =
        jpaRepository.findAllWithRecursiveByPermissions(permissions)
}
