package spring.webmvc.application.dto.result

import com.fasterxml.jackson.annotation.JsonCreator
import spring.webmvc.domain.model.entity.Ticket
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

class TicketResult : ProductResult {
    val ticketId: Long
    val place: String
    val performanceTime: Instant
    val duration: String
    val ageLimit: String

    @JsonCreator
    constructor(
        id: Long,
        name: String,
        description: String,
        price: Long,
        quantity: Long,
        createdAt: Instant,
        ticketId: Long,
        place: String,
        performanceTime: Instant,
        duration: String,
        ageLimit: String
    ) : super(
        id = id,
        category = Category.TICKET,
        name = name,
        description = description,
        price = price,
        quantity = quantity,
        createdAt = createdAt,
    ) {
        this.ticketId = ticketId
        this.place = place
        this.performanceTime = performanceTime
        this.duration = duration
        this.ageLimit = ageLimit
    }

    constructor(ticket: Ticket) : super(product = ticket.product) {
        this.ticketId = checkNotNull(ticket.id)
        this.place = ticket.place
        this.performanceTime = ticket.performanceTime
        this.duration = ticket.duration
        this.ageLimit = ticket.ageLimit
    }
}