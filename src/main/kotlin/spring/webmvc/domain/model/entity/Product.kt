package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.model.enums.Category

@Entity
@Table(name = "product")
class Product(
    @Enumerated(EnumType.STRING)
    val category: Category,

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
        ) = Product(
            category = category,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
        )
    }

    fun update(name: String, description: String, price: Long, quantity: Long) {
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