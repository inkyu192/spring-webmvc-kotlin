package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.presentation.exception.InsufficientQuantityException

@Entity
class Product protected constructor(
    @Enumerated(EnumType.STRING)
    val category: Category,
    name: String,
    description: String,
    price: Int,
    quantity: Int,
) : BaseCreator() {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    var name: String = name
        protected set

    var description: String = description
        protected set

    var price: Int = price
        protected set

    var quantity: Int = quantity
        protected set

    companion object {
        fun create(name: String, description: String, price: Int, quantity: Int, category: Category) =
            Product(name = name, description = description, price = price, quantity = quantity, category = category)
    }

    fun update(name: String, description: String, price: Int, quantity: Int) {
        this.name = name
        this.description = description
        this.price = price
        this.quantity = quantity
    }

    fun removeQuantity(quantity: Int) {
        val differenceQuantity = this.quantity - quantity

        if (differenceQuantity < 0) {
            throw InsufficientQuantityException(
                productName = name,
                requestedQuantity = quantity,
                availableStock = this.quantity,
            )
        }

        this.quantity = differenceQuantity
    }

    fun addQuantity(quantity: Int) {
        this.quantity += quantity
    }
}