package spring.webmvc.domain.model.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.ConstraintMode
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
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

    var place: String = place
        protected set

    var performanceTime: Instant = performanceTime
        protected set

    var duration: String = duration
        protected set

    var ageLimit: String = ageLimit
        protected set

    companion object {
        fun create(
            name: String,
            description: String,
            price: Int,
            quantity: Int,
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
                category = Category.FLIGHT,
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
        price: Int,
        quantity: Int,
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