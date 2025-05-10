package spring.webmvc.application.dto.command

import spring.webmvc.presentation.dto.request.TicketCreateRequest

class TicketCreateCommand(
    ticketCreateRequest: TicketCreateRequest
) : ProductCreateCommand(
    category = ticketCreateRequest.category,
    name = ticketCreateRequest.name,
    description = ticketCreateRequest.description,
    price = ticketCreateRequest.price,
    quantity = ticketCreateRequest.quantity
) {
    val place = ticketCreateRequest.place
    val performanceTime = ticketCreateRequest.performanceTime
    val duration = ticketCreateRequest.duration
    val ageLimit = ticketCreateRequest.ageLimit
}