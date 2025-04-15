package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "order_item")
class OrderItem protected constructor(
    val orderPrice: Int,
    val count: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var item: Item,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var order: Order,
) {
    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    var id: Long? = null
        protected set

    companion object {
        fun create(order: Order, item: Item, count: Int) =
            OrderItem(
                orderPrice = item.price,
                count = count,
                item = item,
                order = order
            ).also {
                item.removeQuantity(count)
            }
    }

    fun cancel() {
        item.addQuantity(count)
    }
}