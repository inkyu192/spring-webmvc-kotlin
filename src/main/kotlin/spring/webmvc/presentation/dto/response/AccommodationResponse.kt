package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.AccommodationResult
import spring.webmvc.domain.model.entity.Accommodation
import java.time.Instant

class AccommodationResponse : ProductResponse {
    val accommodationId: Long
    val place: String
    val checkInTime: Instant
    val checkOutTime: Instant

    constructor(accommodationResult: AccommodationResult) : super(productResult = accommodationResult) {
        accommodationId = accommodationResult.id
        place = accommodationResult.place
        checkInTime = accommodationResult.checkInTime
        checkOutTime = accommodationResult.checkOutTime
    }

    constructor(accommodation: Accommodation) : super(product = accommodation.product) {
        accommodationId = checkNotNull(accommodation.id)
        place = accommodation.place
        checkInTime = accommodation.checkInTime
        checkOutTime = accommodation.checkOutTime
    }
}