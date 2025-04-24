package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.model.entity.Ticket
import spring.webmvc.domain.repository.TicketRepository
import spring.webmvc.presentation.dto.request.TicketCreateRequest
import spring.webmvc.presentation.dto.request.TicketUpdateRequest
import spring.webmvc.presentation.dto.response.TicketResponse
import spring.webmvc.presentation.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class TicketService(
    private val ticketRepository: TicketRepository,
) {
    fun findTicket(id: Long): TicketResponse {
        val ticket = ticketRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(clazz = TicketRepository::class.java, id = id)

        return TicketResponse(ticket = ticket)
    }

    @Transactional
    fun createTicket(ticketCreateRequest: TicketCreateRequest): TicketResponse {
        val ticket = ticketRepository.save(
            Ticket.create(
                name = ticketCreateRequest.name,
                description = ticketCreateRequest.description,
                price = ticketCreateRequest.price,
                quantity = ticketCreateRequest.quantity,
                place = ticketCreateRequest.place,
                performanceTime = ticketCreateRequest.performanceTime,
                duration = ticketCreateRequest.duration,
                ageLimit = ticketCreateRequest.ageLimit,
            )
        )

        return TicketResponse(ticket = ticket)
    }

    @Transactional
    fun updateTicket(id: Long, ticketUpdateRequest: TicketUpdateRequest): TicketResponse {
        val ticket = ticketRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(clazz = TicketRepository::class.java, id = id)

        ticket.update(
            name = ticketUpdateRequest.name,
            description = ticketUpdateRequest.description,
            price = ticketUpdateRequest.price,
            quantity = ticketUpdateRequest.quantity,
            place = ticketUpdateRequest.place,
            performanceTime = ticketUpdateRequest.performanceTime,
            duration = ticketUpdateRequest.duration,
            ageLimit = ticketUpdateRequest.ageLimit,
        )

        return TicketResponse(ticket = ticket)
    }

    @Transactional
    fun deleteTicket(id: Long) {
        val ticket = ticketRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(clazz = TicketRepository::class.java, id = id)

        ticketRepository.delete(ticket)
    }
}