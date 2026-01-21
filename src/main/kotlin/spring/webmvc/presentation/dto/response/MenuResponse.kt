package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.MenuResult

data class MenuResponse(
    val id: Long,
    val name: String,
    val path: String?,
    val children: List<MenuResponse>,
) {
    companion object {
        fun of(menuResult: MenuResult): MenuResponse =
            MenuResponse(
                id = menuResult.id,
                name = menuResult.name,
                path = menuResult.path,
                children = menuResult.children.map { of(menuResult = it) }
            )
    }
}