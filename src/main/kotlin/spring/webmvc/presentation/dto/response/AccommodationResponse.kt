package spring.webmvc.presentation.dto.response

import spring.webmvc.domain.model.entity.Accommodation
import java.time.Instant

data class AccommodationResponse(
    val id: Long,
    val name: String,
    val description: String,
    val price: Int,
    val quantity: Int,
    val createdAt: Instant,
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
) {
    constructor(accommodation: Accommodation) : this(
        id = checkNotNull(accommodation.id),
        name = accommodation.product.name,
        description = accommodation.product.description,
        price = accommodation.product.price,
        quantity = accommodation.product.quantity,
        createdAt = accommodation.product.createdAt,
        place = accommodation.place,
        checkInTime = accommodation.checkInTime,
        checkOutTime = accommodation.checkOutTime,
    )
}