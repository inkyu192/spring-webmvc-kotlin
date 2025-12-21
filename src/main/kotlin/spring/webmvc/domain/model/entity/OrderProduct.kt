package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class OrderProduct protected constructor(
    val orderPrice: Long,
    val quantity: Long,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    val order: Order,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    companion object {
        fun create(order: Order, product: Product, quantity: Long) =
            OrderProduct(
                orderPrice = product.price,
                quantity = quantity,
                product = product,
                order = order
            ).also { product.removeQuantity(quantity = quantity) }
    }

    fun cancel() {
        product.addQuantity(quantity = quantity)
    }
}