package spring.webmvc.application.dto.command

import spring.webmvc.presentation.dto.request.AccommodationUpdateRequest

class AccommodationUpdateCommand(
    accommodationUpdateRequest: AccommodationUpdateRequest
) : ProductUpdateCommand(
    category = accommodationUpdateRequest.category,
    name = accommodationUpdateRequest.name,
    description = accommodationUpdateRequest.description,
    price = accommodationUpdateRequest.price,
    quantity = accommodationUpdateRequest.quantity
) {
    val place = accommodationUpdateRequest.place
    val checkInTime = accommodationUpdateRequest.checkInTime
    val checkOutTime = accommodationUpdateRequest.checkOutTime
}