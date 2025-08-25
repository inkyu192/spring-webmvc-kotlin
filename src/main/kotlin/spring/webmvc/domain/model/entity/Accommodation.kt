package spring.webmvc.domain.model.entity

import jakarta.persistence.Entity
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

@Entity
class Accommodation protected constructor(
    place: String,
    checkInTime: Instant,
    checkOutTime: Instant,
    name: String,
    description: String,
    price: Long,
    quantity: Long,
) : Product(
    category = Category.ACCOMMODATION,
    name = name,
    description = description,
    price = price,
    quantity = quantity
) {
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
            name = name,
            description = description,
            price = price,
            quantity = quantity
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
        super.update(name, description, price, quantity)
        this.place = place
        this.checkInTime = checkInTime
        this.checkOutTime = checkOutTime
    }
}