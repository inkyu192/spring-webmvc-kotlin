package spring.webmvc.application.dto

import spring.webmvc.domain.model.entity.Ticket
import java.time.Instant

data class TicketDto(
    val id: Long,
    val name: String,
    val description: String,
    val price: Int,
    val quantity: Int,
    val createdAt: Instant,
    val place: String,
    val performanceTime: Instant,
    val duration: String,
    val ageLimit: String,
) {
    constructor(ticket: Ticket) : this(
        id = checkNotNull(ticket.id),
        name = ticket.product.name,
        description = ticket.product.description,
        price = ticket.product.price,
        quantity = ticket.product.quantity,
        createdAt = ticket.product.createdAt,
        place = ticket.place,
        performanceTime = ticket.performanceTime,
        duration = ticket.duration,
        ageLimit = ticket.ageLimit,
    )
}
