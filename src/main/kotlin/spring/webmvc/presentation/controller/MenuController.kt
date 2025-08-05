package spring.webmvc.presentation.controller

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.MenuService
import spring.webmvc.presentation.dto.request.MenuCreateRequest
import spring.webmvc.presentation.dto.response.MenuResponse

@RestController
@RequestMapping("/menus")
class MenuController(
    private val menuService: MenuService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createMenu(@RequestBody @Validated menuCreateRequest: MenuCreateRequest) =
        MenuResponse(
            menuResult = menuService.createMenu(
                parentId = menuCreateRequest.parentId,
                name = menuCreateRequest.name,
                path = menuCreateRequest.path,
            )
        )

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    fun findMenus() = menuService.findMenus().map { MenuResponse(menuResult = it) }
}