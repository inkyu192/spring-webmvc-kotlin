package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.model.enums.ProductStatus

@Entity
@Table(name = "product")
class Product(
    @Enumerated(EnumType.STRING)
    val category: Category,

    @Enumerated(EnumType.STRING)
    var status: ProductStatus,

    var name: String,
    var description: String,
    var price: Long,
    var quantity: Long,
) : BaseCreator() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    companion object {
        fun create(
            category: Category,
            name: String,
            description: String,
            price: Long,
            quantity: Long,
            status: ProductStatus = ProductStatus.PENDING,
        ) = Product(
            category = category,
            status = status,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
        )
    }

    fun update(status: ProductStatus, name: String, description: String, price: Long, quantity: Long) {
        this.status = status
        this.name = name
        this.description = description
        this.price = price
        this.quantity = quantity
    }

    fun removeQuantity(quantity: Long) {
        this.quantity -= quantity
    }

    fun addQuantity(quantity: Long) {
        this.quantity += quantity
    }
}