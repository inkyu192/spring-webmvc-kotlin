package spring.webmvc.application.dto.result

import spring.webmvc.domain.model.cache.ProductCache
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

open class ProductResult(
    val id: Long,
    val category: Category,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val createdAt: Instant
) {
    constructor(product: Product) : this(
        id = checkNotNull(product.id),
        category = product.category,
        name = product.name,
        description = product.description,
        price = product.price,
        quantity = product.quantity,
        createdAt = product.createdAt
    )

    constructor(product: ProductCache) : this(
        id = product.id,
        category = product.category,
        name = product.name,
        description = product.description,
        price = product.price,
        quantity = product.quantity,
        createdAt = product.createdAt
    )
}