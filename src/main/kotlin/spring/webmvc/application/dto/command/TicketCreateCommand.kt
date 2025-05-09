package spring.webmvc.application.dto.command

import spring.webmvc.presentation.dto.request.NewTicketCreateRequest

class TicketCreateCommand(
    newTicketCreateRequest: NewTicketCreateRequest
) : ProductCreateCommand(
    category = newTicketCreateRequest.category,
    name = newTicketCreateRequest.name,
    description = newTicketCreateRequest.description,
    price = newTicketCreateRequest.price,
    quantity = newTicketCreateRequest.quantity
) {
    val place = newTicketCreateRequest.place
    val performanceTime = newTicketCreateRequest.performanceTime
    val duration = newTicketCreateRequest.duration
    val ageLimit = newTicketCreateRequest.ageLimit
}