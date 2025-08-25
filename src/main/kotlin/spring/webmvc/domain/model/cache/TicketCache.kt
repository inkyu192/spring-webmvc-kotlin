package spring.webmvc.domain.model.cache

import java.time.Instant

data class TicketCache(
    val id: Long,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val createdAt: Instant,
    val ticketId: Long,
    val place: String,
    val performanceTime: Instant,
    val duration: String,
    val ageLimit: String,
) {
    companion object {
        fun create(
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
            ageLimit: String,
        ) = TicketCache(
            id = id,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            createdAt = createdAt,
            ticketId = ticketId,
            place = place,
            performanceTime = performanceTime,
            duration = duration,
            ageLimit = ageLimit,
        )
    }
}