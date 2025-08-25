package spring.webmvc.application.dto.result

import spring.webmvc.domain.model.cache.AccommodationCache
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

class AccommodationResult : ProductResult {
    val accommodationId: Long
    val place: String
    val checkInTime: Instant
    val checkOutTime: Instant

    constructor(accommodation: Accommodation) : super(product = accommodation) {
        this.accommodationId = checkNotNull(accommodation.id)
        this.place = accommodation.place
        this.checkInTime = accommodation.checkInTime
        this.checkOutTime = accommodation.checkOutTime
    }

    constructor(accommodationCache: AccommodationCache) : super(
        id = accommodationCache.id,
        category = Category.ACCOMMODATION,
        name = accommodationCache.name,
        description = accommodationCache.description,
        price = accommodationCache.price,
        quantity = accommodationCache.quantity,
        createdAt = accommodationCache.createdAt
    ) {
        this.accommodationId = accommodationCache.accommodationId
        this.place = accommodationCache.place
        this.checkInTime = accommodationCache.checkInTime
        this.checkOutTime = accommodationCache.checkOutTime
    }
}