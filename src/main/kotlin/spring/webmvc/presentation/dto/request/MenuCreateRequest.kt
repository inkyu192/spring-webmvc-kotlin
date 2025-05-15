package spring.webmvc.presentation.dto.request

data class MenuCreateRequest(
    val name: String,
    val path: String,
    val parentId: Long? = null,
    val permissionIds: List<Long> = emptyList(),
)
