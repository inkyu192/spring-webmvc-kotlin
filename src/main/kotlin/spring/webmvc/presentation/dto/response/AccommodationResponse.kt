package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.AccommodationResult
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
}