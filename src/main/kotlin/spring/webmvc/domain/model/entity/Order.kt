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
    member: Member
) : BaseTime() {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    var id: Long? = null
        protected set

    @Enumerated(EnumType.STRING)
    var status: OrderStatus = status
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var member: Member = member
        protected set

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    private val _orderItems: MutableList<OrderItem> = mutableListOf()

    @get:Transient
    val orderItems: List<OrderItem>
        get() = _orderItems.toList()

    companion object {
        fun create(member: Member, orderItems: List<OrderItem>) =
            Order(
                orderedAt = Instant.now(),
                status = OrderStatus.ORDER,
                member = member,
            ).apply { orderItems.forEach { associateItem(it) } }
    }

    fun associateItem(orderItem: OrderItem) {
        _orderItems.add(orderItem)
        orderItem.associateOrder(this)
    }

    fun cancel() {
        val id = checkNotNull(this.id)

        if (status == OrderStatus.CONFIRM) {
            throw OrderCancelNotAllowedException(id)
        }

        status = OrderStatus.CANCEL
        orderItems.forEach { it.cancel() }
    }
}
