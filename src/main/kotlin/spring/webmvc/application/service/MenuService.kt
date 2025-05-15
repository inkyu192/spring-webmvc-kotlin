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
    private val permissionService: PermissionService,
) {
    @Transactional
    fun createMenu(parentId: Long?, name: String, path: String?, permissionIds: List<Long>): MenuResult {
        val menu = Menu.create(name, path)

        val parent = if (parentId != null) {
            menuRepository.findByIdOrNull(parentId)
                ?: throw EntityNotFoundException(kClass = Menu::class, id = parentId)
        } else {
            null
        }

        if (parent != null) {
            menu.updateParent(parent)
        }

        permissionService.addPermission(
            permissionIds = permissionIds,
            consumer = menu::addPermission,
        )

        return MenuResult(menu = menuRepository.save(menu))
    }

    fun findMenus(): List<MenuResult> {
        val permissions = SecurityContextUtil.getAuthorities()

        return menuRepository.findAllByPermissionNameIn(permissions)
            .filter { it.parent == null }
            .map { MenuResult(menu = it) }
    }
}