package spring.webmvc.application.dto.command

data class CurationProductCreateCommand(
    val productId: Long,
    val sortOrder: Long,
)