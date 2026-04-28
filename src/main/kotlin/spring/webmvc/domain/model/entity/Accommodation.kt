package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
class Accommodation protected constructor(
    @field:MapsId
    @field:OneToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "product_id")
    val product: Product,

    place: String,
    checkInTime: Instant,
    checkOutTime: Instant,
) {
    @Id
    var productId: Long? = null
        protected set

    var place = place
        protected set

    var checkInTime = checkInTime
        protected set

    var checkOutTime = checkOutTime
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
