package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
class Transport protected constructor(
    @field:MapsId
    @field:OneToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "product_id")
    val product: Product,

    departureLocation: String,
    arrivalLocation: String,
    departureTime: Instant,
    arrivalTime: Instant,
) {
    @Id
    var productId: Long? = null
        protected set

    var departureLocation = departureLocation
        protected set

    var arrivalLocation = arrivalLocation
        protected set

    var departureTime = departureTime
        protected set

    var arrivalTime = arrivalTime
        protected set

    companion object {
        fun create(
            product: Product,
            departureLocation: String,
            arrivalLocation: String,
            departureTime: Instant,
            arrivalTime: Instant,
        ) = Transport(
            product = product,
            departureLocation = departureLocation,
            arrivalLocation = arrivalLocation,
            departureTime = departureTime,
            arrivalTime = arrivalTime,
        )
    }

    fun update(
        departureLocation: String,
        arrivalLocation: String,
        departureTime: Instant,
        arrivalTime: Instant,
    ) {
        this.departureLocation = departureLocation
        this.arrivalLocation = arrivalLocation
        this.departureTime = departureTime
        this.arrivalTime = arrivalTime
    }
}
