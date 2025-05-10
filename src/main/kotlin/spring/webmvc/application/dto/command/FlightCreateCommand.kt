package spring.webmvc.application.dto.command

import spring.webmvc.presentation.dto.request.FlightCreateRequest

class FlightCreateCommand(
    flightCreateRequest: FlightCreateRequest
) : ProductCreateCommand(
    category = flightCreateRequest.category,
    name = flightCreateRequest.name,
    description = flightCreateRequest.description,
    price = flightCreateRequest.price,
    quantity = flightCreateRequest.quantity
) {
    val airline = flightCreateRequest.airline
    val flightNumber = flightCreateRequest.flightNumber
    val departureAirport = flightCreateRequest.departureAirport
    val arrivalAirport = flightCreateRequest.arrivalAirport
    val departureTime = flightCreateRequest.departureTime
    val arrivalTime = flightCreateRequest.arrivalTime
}