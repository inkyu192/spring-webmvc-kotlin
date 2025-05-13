package spring.webmvc.application.dto.command

import spring.webmvc.presentation.dto.request.FlightUpdateRequest

class FlightUpdateCommand(
    flightUpdateRequest: FlightUpdateRequest
) : ProductUpdateCommand(
    category = flightUpdateRequest.category,
    name = flightUpdateRequest.name,
    description = flightUpdateRequest.description,
    price = flightUpdateRequest.price,
    quantity = flightUpdateRequest.quantity
) {
    val airline = flightUpdateRequest.airline
    val flightNumber = flightUpdateRequest.flightNumber
    val departureAirport = flightUpdateRequest.departureAirport
    val arrivalAirport = flightUpdateRequest.arrivalAirport
    val departureTime = flightUpdateRequest.departureTime
    val arrivalTime = flightUpdateRequest.arrivalTime
}