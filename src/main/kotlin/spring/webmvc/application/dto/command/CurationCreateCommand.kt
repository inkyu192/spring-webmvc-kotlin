package spring.webmvc.application.dto.command

import spring.webmvc.domain.model.enums.CurationCategory

data class CurationCreateCommand(
    val title: String,
    val category: CurationCategory,
    val isExposed: Boolean,
    val sortOrder: Long,
    val products: List<CurationProductCreateCommand>,
)