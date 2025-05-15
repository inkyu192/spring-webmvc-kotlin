package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.MenuResult

data class MenuResponse(
    val id: Long,
    val name: String,
    val path: String?,
    val children: List<MenuResponse>,
) {
    constructor(menuResult: MenuResult) : this(
        id = menuResult.id,
        name = menuResult.name,
        path = menuResult.path,
        children = menuResult.children.map { MenuResponse(menuResult = it) }
    )
}