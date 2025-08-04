package spring.webmvc.application.dto.command

data class CurationCreateCommand(
    val title: String,
    val isExposed: Boolean,
    val sortOrder: Long,
    val products: List<CurationProductCreateCommand>,
)