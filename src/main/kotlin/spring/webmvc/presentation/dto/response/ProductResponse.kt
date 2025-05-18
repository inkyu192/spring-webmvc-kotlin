package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

open class ProductResponse(
    val id: Long,
    val category: Category,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val createdAt: Instant
) {
    constructor(productResult: ProductResult) : this(
        id = productResult.id,
        category = productResult.category,
        name = productResult.name,
        description = productResult.description,
        price = productResult.price,
        quantity = productResult.quantity,
        createdAt = productResult.createdAt
    )

    constructor(product: Product) : this(
        id = checkNotNull(product.id),
        category = product.category,
        name = product.name,
        description = product.description,
        price = product.price,
        quantity = product.quantity,
        createdAt = product.createdAt
    )
}
