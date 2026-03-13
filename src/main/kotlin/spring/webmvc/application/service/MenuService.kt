package spring.webmvc.application.service

import org.springframework.context.i18n.LocaleContextHolder
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
    private val translationService: TranslationService,
) {
    fun findMenus(): List<MenuResult> {
        val permissions = SecurityContextUtil.getAuthorities()

        if (permissions.isEmpty()) {
            return listOf()
        }

        val allMenus = menuRepository.findAllWithRecursiveByPermissions(permissions)
        val rootMenus = allMenus.filter { it.parent == null }
        val locale = LocaleContextHolder.getLocale()

        return rootMenus.map { mapToResult(menu = it, allMenus = allMenus, locale = locale) }
    }

    private fun mapToResult(menu: Menu, allMenus: List<Menu>, locale: java.util.Locale): MenuResult {
        val children = allMenus.filter { it.parent?.id == menu.id }
            .sortedBy { it.sortOrder }
            .map { mapToResult(menu = it, allMenus = allMenus, locale = locale) }

        return MenuResult(
            id = checkNotNull(menu.id),
            name = translationService.getMessage(code = menu.translationCode, locale = locale),
            path = menu.path,
            children = children,
        )
    }
}
