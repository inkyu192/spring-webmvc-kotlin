package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Ticket
import spring.webmvc.domain.repository.TicketRepository
import spring.webmvc.infrastructure.persistence.TicketJpaRepository

@Component
class TicketRepositoryAdapter(
    private val jpaRepository: TicketJpaRepository,
): TicketRepository {
    override fun findByIdOrNull(id: Long) = jpaRepository.findByIdOrNull(id)

    override fun save(ticket: Ticket) = jpaRepository.save(ticket)

    override fun delete(ticket: Ticket) {
        jpaRepository.delete(ticket)
    }
}