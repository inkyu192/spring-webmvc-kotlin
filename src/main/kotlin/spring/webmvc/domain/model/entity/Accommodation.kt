package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

@Entity
class Accommodation protected constructor(
    place: String,
    checkInTime: Instant,
    checkOutTime: Instant,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "product_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val product: Product,
) {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    var place = place
        protected set

    var checkInTime = checkInTime
        protected set

    var checkOutTime = checkOutTime
        protected set

    companion object {
        fun create(
            name: String,
            description: String,
            price: Long,
            quantity: Long,
            place: String,
            checkInTime: Instant,
            checkOutTime: Instant,
        ) = Accommodation(
            place = place,
            checkInTime = checkInTime,
            checkOutTime = checkOutTime,
            product = Product.create(
                category = Category.ACCOMMODATION,
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
        checkInTime: Instant,
        checkOutTime: Instant,
    ) {
        this.product.update(name, description, price, quantity)
        this.place = place
        this.checkInTime = checkInTime
        this.checkOutTime = checkOutTime
    }
}