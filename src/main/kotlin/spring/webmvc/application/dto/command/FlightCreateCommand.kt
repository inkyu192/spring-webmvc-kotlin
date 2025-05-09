package spring.webmvc.application.dto.command

import spring.webmvc.presentation.dto.request.NewFlightCreateRequest

class FlightCreateCommand(
    newFlightCreateRequest: NewFlightCreateRequest
) : ProductCreateCommand(
    category = newFlightCreateRequest.category,
    name = newFlightCreateRequest.name,
    description = newFlightCreateRequest.description,
    price = newFlightCreateRequest.price,
    quantity = newFlightCreateRequest.quantity
) {
    val airline = newFlightCreateRequest.airline
    val flightNumber = newFlightCreateRequest.flightNumber
    val departureAirport = newFlightCreateRequest.departureAirport
    val arrivalAirport = newFlightCreateRequest.arrivalAirport
    val departureTime = newFlightCreateRequest.departureTime
    val arrivalTime = newFlightCreateRequest.arrivalTime
}