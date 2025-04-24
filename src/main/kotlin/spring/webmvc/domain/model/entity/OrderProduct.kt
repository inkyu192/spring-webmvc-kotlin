package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class OrderProduct protected constructor(
    val orderPrice: Int,
    val count: Int,

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
        fun create(order: Order, product: Product, count: Int) =
            OrderProduct(
                orderPrice = product.price,
                count = count,
                product = product,
                order = order
            ).also { product.removeQuantity(quantity = count) }
    }

    fun cancel() { product.addQuantity(quantity = count) }
}