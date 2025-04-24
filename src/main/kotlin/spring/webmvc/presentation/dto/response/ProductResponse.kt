package spring.webmvc.presentation.dto.response

import spring.webmvc.domain.model.entity.Product
import java.time.Instant

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    val price: Int,
    val quantity: Int,
    val createdAt: Instant,
) {
    constructor(product: Product) : this(
        id = checkNotNull(product.id),
        name = product.name,
        description = product.description,
        price = product.price,
        quantity = product.quantity,
        createdAt = product.createdAt,
    )
}
