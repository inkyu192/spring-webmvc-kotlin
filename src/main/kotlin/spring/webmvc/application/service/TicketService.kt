package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.model.entity.Ticket
import spring.webmvc.domain.repository.TicketRepository
import spring.webmvc.presentation.exception.EntityNotFoundException
import java.time.Instant

@Service
@Transactional(readOnly = true)
class TicketService(
    private val ticketRepository: TicketRepository,
) {
    @Transactional
    fun updateTicket(
        id: Long,
        name: String,
        description: String,
        price: Int,
        quantity: Int,
        place: String,
        performanceTime: Instant,
        duration: String,
        ageLimit: String,
    ): Ticket {
        val ticket = ticketRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(kClass = TicketRepository::class, id = id)

        ticket.update(
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            place = place,
            performanceTime = performanceTime,
            duration = duration,
            ageLimit = ageLimit,
        )

        return ticket
    }

    @Transactional
    fun deleteTicket(id: Long) {
        val ticket = ticketRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(kClass = TicketRepository::class, id = id)

        ticketRepository.delete(ticket)
    }
}