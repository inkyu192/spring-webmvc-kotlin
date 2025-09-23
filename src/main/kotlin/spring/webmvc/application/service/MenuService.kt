package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.result.MenuResult
import spring.webmvc.domain.model.entity.Menu
import spring.webmvc.domain.repository.MenuRepository
import spring.webmvc.infrastructure.security.SecurityContextUtil

@Service
@Transactional(readOnly = true)
class MenuService(
    private val menuRepository: MenuRepository,
) {
    fun findMenus(): List<MenuResult> {
        val permissions = SecurityContextUtil.getAuthorities()

        if (permissions.isEmpty()) {
            return listOf()
        }

        val allMenus = getParentMenus(menuRepository.findByPermissions(permissions))
        val rootMenus = allMenus.filter { it.parent == null }

        return rootMenus.map { mapToResult(menu = it, allMenus = allMenus) }
    }

    fun getParentMenus(menus: List<Menu>): List<Menu> {
        val parentIds = menus.mapNotNull { it.parent?.id }.distinct()
        if (parentIds.isEmpty()) {
            return menus
        }
        val parentMenus = menuRepository.findAllById(parentIds)
        return (menus + getParentMenus(parentMenus)).distinctBy { it.id }
    }

    private fun mapToResult(menu: Menu, allMenus: List<Menu>): MenuResult {
        val childMenus = allMenus.filter { it.parent?.id == menu.id }

        return MenuResult(
            id = checkNotNull(menu.id),
            name = menu.name,
            path = menu.path,
            children = childMenus
                .sortedBy { it.sortOrder }
                .map { mapToResult(menu = it, allMenus = allMenus) }
        )
    }
}