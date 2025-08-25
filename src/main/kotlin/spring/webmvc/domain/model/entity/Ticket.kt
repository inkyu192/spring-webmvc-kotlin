package spring.webmvc.domain.model.entity

import jakarta.persistence.Entity
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

@Entity
class Ticket protected constructor(
    place: String,
    performanceTime: Instant,
    duration: String,
    ageLimit: String,
    name: String,
    description: String,
    price: Long,
    quantity: Long,
) : Product(
    category = Category.TICKET,
    name = name,
    description = description,
    price = price,
    quantity = quantity
) {
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
        performanceTime: Instant,
        duration: String,
        ageLimit: String,
    ) {
        super.update(name, description, price, quantity)
        this.place = place
        this.performanceTime = performanceTime
        this.duration = duration
        this.ageLimit = ageLimit
    }
}