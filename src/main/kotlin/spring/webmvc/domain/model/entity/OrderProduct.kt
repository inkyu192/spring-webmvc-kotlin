package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class OrderProduct protected constructor(
    val orderPrice: Int,
    val quantity: Int,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val product: Product,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val order: Order,
) {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    companion object {
        fun create(order: Order, product: Product, quantity: Int) =
            OrderProduct(
                orderPrice = product.price,
                quantity = quantity,
                product = product,
                order = order
            ).also { product.removeQuantity(quantity = quantity) }
    }

    fun cancel() { product.addQuantity(quantity = quantity) }
}