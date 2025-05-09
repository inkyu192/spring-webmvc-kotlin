package spring.webmvc.application.dto.command

import spring.webmvc.domain.model.enums.Category

open class ProductCreateCommand(
    val category: Category,
    val name: String,
    val description: String,
    val price: Int,
    val quantity: Int
)