package spring.webmvc.application.dto.command

import spring.webmvc.presentation.dto.request.NewAccommodationCreateRequest

class AccommodationCreateCommand(
    newAccommodationCreateRequest: NewAccommodationCreateRequest
) : ProductCreateCommand(
    category = newAccommodationCreateRequest.category,
    name = newAccommodationCreateRequest.name,
    description = newAccommodationCreateRequest.description,
    price = newAccommodationCreateRequest.price,
    quantity = newAccommodationCreateRequest.quantity
) {
    val place = newAccommodationCreateRequest.place
    val checkInTime = newAccommodationCreateRequest.checkInTime
    val checkOutTime = newAccommodationCreateRequest.checkOutTime
}