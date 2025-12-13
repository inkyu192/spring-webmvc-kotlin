package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.infrastructure.exception.OrderCancelNotAllowedException
import java.time.Instant

@Entity
@Table(name = "orders")
class Order protected constructor(
    val orderedAt: Instant,
    status: OrderStatus,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,
) : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Enumerated(EnumType.STRING)
    var status = status
        protected set

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val _orderProducts = mutableListOf<OrderProduct>()

    @get:Transient
    val orderProducts: List<OrderProduct>
        get() = _orderProducts.toList()

    companion object {
        fun create(user: User) = Order(
            orderedAt = Instant.now(),
            status = OrderStatus.ORDER,
            user = user,
        )
    }

    fun addProduct(product: Product, quantity: Long) {
        _orderProducts.add(OrderProduct.create(order = this, product = product, quantity = quantity))
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
