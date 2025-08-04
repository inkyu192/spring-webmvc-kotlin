package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

@Entity
class Ticket protected constructor(
    place: String,
    performanceTime: Instant,
    duration: String,
    ageLimit: String,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "product_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val product: Product,
) {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    var place = place
        protected set

    var performanceTime = performanceTime
        protected set

    var duration = duration
        protected set

    var ageLimit = ageLimit
        protected set

    companion object {
        fun create(
            name: String,
            description: String,
            price: Long,
            quantity: Long,
            place: String,
            performanceTime: Instant,
            duration: String,
            ageLimit: String,
        ) = Ticket(
            place = place,
            performanceTime = performanceTime,
            duration = duration,
            ageLimit = ageLimit,
            product = Product.create(
                category = Category.TICKET,
                name = name,
                description = description,
                price = price,
                quantity = quantity
            ),
        )
    }

    fun update(
        name: String,
        description: String,
        price: Long,
        quantity: Long,
        place: String,
        performanceTime: Instant,
        duration: String,
        ageLimit: String,
    ) {
        this.product.update(name, description, price, quantity)
        this.place = place
        this.performanceTime = performanceTime
        this.duration = duration
        this.ageLimit = ageLimit
    }
}