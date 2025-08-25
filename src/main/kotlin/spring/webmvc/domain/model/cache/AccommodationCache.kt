package spring.webmvc.domain.model.cache

import java.time.Instant

data class AccommodationCache(
    val id: Long,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val createdAt: Instant,
    val accommodationId: Long,
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
) {
    companion object {
        fun create(
            id: Long,
            name: String,
            description: String,
            price: Long,
            quantity: Long,
            createdAt: Instant,
            accommodationId: Long,
            place: String,
            checkInTime: Instant,
            checkOutTime: Instant,
        ) = AccommodationCache(
            id = id,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            createdAt = createdAt,
            accommodationId = accommodationId,
            place = place,
            checkInTime = checkInTime,
            checkOutTime = checkOutTime,
        )
    }
}