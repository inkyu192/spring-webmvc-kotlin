package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "accommodation")
class Accommodation(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product,

    var place: String,
    var checkInTime: Instant,
    var checkOutTime: Instant,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null
        protected set

    companion object {
        fun create(
            product: Product,
            place: String,
            checkInTime: Instant,
            checkOutTime: Instant,
        ) = Accommodation(
            product = product,
            place = place,
            checkInTime = checkInTime,
            checkOutTime = checkOutTime,
        )
    }

    fun update(
        place: String,
        checkInTime: Instant,
        checkOutTime: Instant,
    ) {
        this.place = place
        this.checkInTime = checkInTime
        this.checkOutTime = checkOutTime
    }
}