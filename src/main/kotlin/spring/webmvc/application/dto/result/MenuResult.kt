package spring.webmvc.application.dto.result

data class MenuResult(
    val id: Long,
    val name: String,
    val path: String?,
    val children: List<MenuResult>,
)
