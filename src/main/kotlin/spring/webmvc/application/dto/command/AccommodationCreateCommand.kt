package spring.webmvc.application.dto.command

import spring.webmvc.presentation.dto.request.AccommodationCreateRequest

class AccommodationCreateCommand(
    accommodationCreateRequest: AccommodationCreateRequest
) : ProductCreateCommand(
    category = accommodationCreateRequest.category,
    name = accommodationCreateRequest.name,
    description = accommodationCreateRequest.description,
    price = accommodationCreateRequest.price,
    quantity = accommodationCreateRequest.quantity
) {
    val place = accommodationCreateRequest.place
    val checkInTime = accommodationCreateRequest.checkInTime
    val checkOutTime = accommodationCreateRequest.checkOutTime
}