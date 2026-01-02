package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "transport")
class Transport(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product,

    var departureLocation: String,
    var arrivalLocation: String,
    var departureTime: Instant,
    var arrivalTime: Instant,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null
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
