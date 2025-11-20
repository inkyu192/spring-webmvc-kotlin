package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Ticket
import spring.webmvc.domain.repository.TicketRepository
import spring.webmvc.infrastructure.extensions.findByIdOrThrow
import spring.webmvc.infrastructure.persistence.jpa.TicketJpaRepository

@Component
class TicketRepositoryAdapter(
    private val jpaRepository: TicketJpaRepository,
) : TicketRepository {
    override fun findById(id: Long): Ticket = jpaRepository.findByIdOrThrow(id)

    override fun save(ticket: Ticket) = jpaRepository.save(ticket)

    override fun delete(ticket: Ticket) {
        jpaRepository.delete(ticket)
    }
}