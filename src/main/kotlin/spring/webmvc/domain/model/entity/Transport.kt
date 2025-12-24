package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

@Entity
@Table(name = "transport")
class Transport(
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product,

    var departureLocation: String,
    var arrivalLocation: String,
    var departureTime: Instant,
    var arrivalTime: Instant,
) {
    @Id
    @Column(name = "product_id")
    var id: Long? = null
        protected set

    companion object {
        fun create(
            name: String,
            description: String,
            price: Long,
            quantity: Long,
            departureLocation: String,
            arrivalLocation: String,
            departureTime: Instant,
            arrivalTime: Instant,
        ): Transport {
            val product = Product.create(
                category = Category.TRANSPORT,
                name = name,
                description = description,
                price = price,
                quantity = quantity
            )
            return Transport(
                product = product,
                departureLocation = departureLocation,
                arrivalLocation = arrivalLocation,
                departureTime = departureTime,
                arrivalTime = arrivalTime
            )
        }
    }

    fun update(
        name: String,
        description: String,
        price: Long,
        quantity: Long,
        departureLocation: String,
        arrivalLocation: String,
        departureTime: Instant,
        arrivalTime: Instant,
    ) {
        product.update(name, description, price, quantity)
        this.departureLocation = departureLocation
        this.arrivalLocation = arrivalLocation
        this.departureTime = departureTime
        this.arrivalTime = arrivalTime
    }
}
