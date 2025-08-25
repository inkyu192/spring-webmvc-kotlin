package spring.webmvc.application.dto.result

import spring.webmvc.domain.model.cache.TicketCache
import spring.webmvc.domain.model.entity.Ticket
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

class TicketResult : ProductResult {
    val ticketId: Long
    val place: String
    val performanceTime: Instant
    val duration: String
    val ageLimit: String

    constructor(ticket: Ticket) : super(product = ticket.product) {
        this.ticketId = checkNotNull(ticket.id)
        this.place = ticket.place
        this.performanceTime = ticket.performanceTime
        this.duration = ticket.duration
        this.ageLimit = ticket.ageLimit
    }

    constructor(ticketCache: TicketCache) : super(
        id = ticketCache.id,
        category = Category.TICKET,
        name = ticketCache.name,
        description = ticketCache.description,
        price = ticketCache.price,
        quantity = ticketCache.quantity,
        createdAt = ticketCache.createdAt
    ) {
        this.ticketId = ticketCache.ticketId
        this.place = ticketCache.place
        this.performanceTime = ticketCache.performanceTime
        this.duration = ticketCache.duration
        this.ageLimit = ticketCache.ageLimit
    }
}