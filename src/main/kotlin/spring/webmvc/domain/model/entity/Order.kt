package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.infrastructure.exception.InvalidEntityStatusException
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
        val orderProduct = OrderProduct.create(order = this, product = product, quantity = quantity)
        _orderProducts.add(orderProduct)
    }

    fun cancel() {
        if (status == OrderStatus.CONFIRM) {
            throw InvalidEntityStatusException(
                kClass = Order::class,
                id = checkNotNull(id),
                fromStatus = status.description,
                toStatus = OrderStatus.CANCEL.description,
            )
        }

        status = OrderStatus.CANCEL

        _orderProducts.forEach { it.cancel() }
    }

    fun updateStatus(status: OrderStatus) {
        if (this.status == OrderStatus.CONFIRM) {
            throw InvalidEntityStatusException(
                kClass = Order::class,
                id = checkNotNull(id),
                fromStatus = this.status.description,
                toStatus = status.description,
            )
        }

        this.status = status

        if (status == OrderStatus.CANCEL) {
            _orderProducts.forEach { it.cancel() }
        }
    }
}
