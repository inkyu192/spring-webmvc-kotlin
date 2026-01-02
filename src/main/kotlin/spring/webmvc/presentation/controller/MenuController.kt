package spring.webmvc.presentation.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import spring.webmvc.application.service.MenuService
import spring.webmvc.presentation.dto.response.MenuResponse

@RestController
@RequestMapping("/menus")
class MenuController(
    private val menuService: MenuService,
) {
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    fun findMenus() = menuService.findMenus().map { MenuResponse.from(it) }
}