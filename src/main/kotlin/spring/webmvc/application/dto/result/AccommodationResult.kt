package spring.webmvc.application.dto.result

import com.fasterxml.jackson.annotation.JsonCreator
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

class AccommodationResult : ProductResult {
    val accommodationId: Long
    val place: String
    val checkInTime: Instant
    val checkOutTime: Instant

    @JsonCreator
    constructor(
        id: Long,
        name: String,
        description: String,
        price: Int,
        quantity: Int,
        createdAt: Instant,
        accommodationId: Long,
        place: String,
        checkInTime: Instant,
        checkOutTime: Instant
    ) : super(
        id = id,
        category = Category.ACCOMMODATION,
        name = name,
        description = description,
        price = price,
        quantity = quantity,
        createdAt = createdAt
    ) {
        this.accommodationId = accommodationId
        this.place = place
        this.checkInTime = checkInTime
        this.checkOutTime = checkOutTime
    }

    constructor(accommodation: Accommodation) : super(product = accommodation.product) {
        this.accommodationId = checkNotNull(accommodation.id)
        this.place = accommodation.place
        this.checkInTime = accommodation.checkInTime
        this.checkOutTime = accommodation.checkOutTime
    }
}