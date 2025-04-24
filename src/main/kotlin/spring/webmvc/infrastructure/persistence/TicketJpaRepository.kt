package spring.webmvc.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.Ticket

interface TicketJpaRepository : JpaRepository<Ticket, Long> {
}