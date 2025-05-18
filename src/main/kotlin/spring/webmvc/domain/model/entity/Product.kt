package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.model.enums.Category

@Entity
class Product protected constructor(
    @Enumerated(EnumType.STRING)
    val category: Category,
    name: String,
    description: String,
    price: Long,
    quantity: Long,
) : BaseCreator() {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    var name = name
        protected set

    var description = description
        protected set

    var price = price
        protected set

    var quantity = quantity
        protected set

    companion object {
        fun create(name: String, description: String, price: Long, quantity: Long, category: Category) =
            Product(name = name, description = description, price = price, quantity = quantity, category = category)
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