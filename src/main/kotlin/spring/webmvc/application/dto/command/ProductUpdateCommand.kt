package spring.webmvc.application.dto.command

import spring.webmvc.domain.model.enums.Category

open class ProductUpdateCommand(
    val category: Category,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
)