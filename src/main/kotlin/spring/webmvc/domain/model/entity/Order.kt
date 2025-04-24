package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.presentation.exception.OrderCancelNotAllowedException
import java.time.Instant

@Entity
@Table(name = "orders")
class Order protected constructor(
    val orderedAt: Instant,
    status: OrderStatus,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val member: Member,
) : BaseTime() {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    @Enumerated(EnumType.STRING)
    var status: OrderStatus = status
        protected set

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    private val _orderProducts: MutableList<OrderProduct> = mutableListOf()

    @get:Transient
    val orderProducts: List<OrderProduct>
        get() = _orderProducts.toList()

    companion object {
        fun create(member: Member) = Order(
            orderedAt = Instant.now(),
            status = OrderStatus.ORDER,
            member = member,
        )
    }

    fun addProduct(product: Product, count: Int) {
        _orderProducts.add(OrderProduct.create(order = this, product = product, count = count))
    }

    fun cancel() {
        val id = checkNotNull(this.id)

        if (status == OrderStatus.CONFIRM) {
            throw OrderCancelNotAllowedException(orderId = id)
        }

        status = OrderStatus.CANCEL
        _orderProducts.forEach { it.cancel() }
    }
}
