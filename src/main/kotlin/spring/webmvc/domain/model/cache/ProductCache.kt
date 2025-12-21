package spring.webmvc.domain.model.cache

import spring.webmvc.domain.model.enums.Category
import java.time.Instant

data class ProductCache(
    val id: Long,
    val category: Category,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val createdAt: Instant,
) {
    companion object {
        fun create(
            id: Long,
            name: String,
            description: String,
            price: Long,
            quantity: Long,
            category: Category,
            createdAt: Instant,
        ) = ProductCache(
            id = id,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            category = category,
            createdAt = createdAt
        )
    }
}