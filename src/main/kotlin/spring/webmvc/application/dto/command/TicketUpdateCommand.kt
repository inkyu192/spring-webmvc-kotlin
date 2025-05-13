package spring.webmvc.application.dto.command

import spring.webmvc.presentation.dto.request.TicketUpdateRequest

class TicketUpdateCommand(
    ticketUpdateRequest: TicketUpdateRequest
) : ProductUpdateCommand(
    category = ticketUpdateRequest.category,
    name = ticketUpdateRequest.name,
    description = ticketUpdateRequest.description,
    price = ticketUpdateRequest.price,
    quantity = ticketUpdateRequest.quantity
) {
    val place = ticketUpdateRequest.place
    val performanceTime = ticketUpdateRequest.performanceTime
    val duration = ticketUpdateRequest.duration
    val ageLimit = ticketUpdateRequest.ageLimit
}