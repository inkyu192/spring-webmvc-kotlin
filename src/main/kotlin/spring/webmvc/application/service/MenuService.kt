package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.result.MenuResult
import spring.webmvc.domain.model.entity.Menu
import spring.webmvc.domain.repository.MenuRepository
import spring.webmvc.infrastructure.security.SecurityContextUtil
import spring.webmvc.presentation.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class MenuService(
    private val menuRepository: MenuRepository,
) {
    @Transactional
    fun createMenu(parentId: Long?, name: String, path: String?): MenuResult {
        val menu = if (parentId == null) {
            Menu.create(name, path)
        } else {
            val parent = menuRepository.findByIdOrNull(parentId)
                ?: throw EntityNotFoundException(kClass = Menu::class, id = parentId)

            Menu.create(name, path, parent)
        }

        menuRepository.save(menu)

        return mapToResult(menu)
    }

    fun findMenus(): List<MenuResult> {
        val permissions = SecurityContextUtil.getAuthorities()

        return menuRepository.findRootMenus(permissions)
            .map { mapToResult(menu = it) }
    }

    private fun mapToResult(menu: Menu): MenuResult {
        val permissions = SecurityContextUtil.getAuthorities()
        val childMenus = menuRepository.findChildMenus(
            permissions = permissions,
            parentId = checkNotNull(menu.id)
        )

        return MenuResult(
            id = checkNotNull(menu.id),
            name = menu.name,
            path = menu.path,
            children = childMenus.map { mapToResult(menu = it) })
    }
}