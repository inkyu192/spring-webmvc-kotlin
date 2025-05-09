package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.presentation.exception.InsufficientQuantityException

@Entity
class Item protected constructor(
    name: String,
    description: String,
    price: Int,
    quantity: Int,
    category: Category,
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

    @Enumerated(EnumType.STRING)
    var category: Category = category
        protected set

    companion object {
        fun create(name: String, description: String, price: Int, quantity: Int, category: Category) =
            Item(
                name = name,
                description = description,
                price = price,
                quantity = quantity,
                category = category,
            )
    }

    fun update(name: String, description: String, price: Int, quantity: Int, category: Category) {
        this.name = name
        this.description = description
        this.price = price
        this.quantity = quantity
        this.category = category
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
