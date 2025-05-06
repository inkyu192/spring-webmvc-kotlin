package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.TicketResult
import spring.webmvc.domain.model.entity.Ticket
import java.time.Instant

class TicketResponse : ProductResponse {
    val ticketId: Long
    val place: String
    val performanceTime: Instant
    val duration: String
    val ageLimit: String

    constructor(ticketResult: TicketResult) : super(productResult = ticketResult) {
        ticketId = ticketResult.id
        place = ticketResult.place
        performanceTime = ticketResult.performanceTime
        duration = ticketResult.duration
        ageLimit = ticketResult.ageLimit
    }

    constructor(ticket: Ticket) : super(product = ticket.product) {
        ticketId = checkNotNull(ticket.id)
        place = ticket.place
        performanceTime = ticket.performanceTime
        duration = ticket.duration
        ageLimit = ticket.ageLimit
    }
}