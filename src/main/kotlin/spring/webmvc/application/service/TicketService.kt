package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.repository.TicketRepository
import spring.webmvc.presentation.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class TicketService(
    private val ticketRepository: TicketRepository,
) {
    @Transactional
    fun deleteTicket(id: Long) {
        val ticket = ticketRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(kClass = TicketRepository::class, id = id)

        ticketRepository.delete(ticket)
    }
}