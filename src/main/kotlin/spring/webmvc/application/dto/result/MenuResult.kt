package spring.webmvc.application.dto.result

import spring.webmvc.domain.model.entity.Menu

data class MenuResult(
    val id: Long,
    val name: String,
    val path: String?,
    val children: List<MenuResult>
) {
    constructor(menu: Menu) : this(
        id = checkNotNull(menu.id),
        name = menu.name,
        path = menu.path,
        children = menu.children.map { MenuResult(menu = it) }
    )
}
