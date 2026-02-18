package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.MenuResult

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
