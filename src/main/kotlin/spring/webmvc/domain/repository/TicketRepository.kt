package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Ticket

interface TicketRepository {
    fun findByIdOrNull(id: Long): Ticket?
    fun save(ticket: Ticket): Ticket
    fun delete(ticket: Ticket)
}