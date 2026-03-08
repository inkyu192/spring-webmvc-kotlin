package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.MenuResult

data class MenuListResponse(
    val size: Long,
    val menus: List<MenuResponse>,
) {
    companion object {
        fun of(results: List<MenuResult>) = MenuListResponse(
            size = results.size.toLong(),
            menus = results.map { MenuResponse.of(it) },
        )
    }
}

data class MenuResponse(
    val id: Long,
    val name: String,
    val path: String?,
    val children: List<MenuResponse>,
) {
    companion object {
        fun of(result: MenuResult): MenuResponse =
            MenuResponse(
                id = result.id,
                name = result.name,
                path = result.path,
                children = result.children.map { of(result = it) }
            )
    }
}
