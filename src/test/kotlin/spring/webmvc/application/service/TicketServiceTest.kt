package spring.webmvc.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import spring.webmvc.domain.model.entity.Ticket
import spring.webmvc.domain.repository.TicketRepository
import spring.webmvc.presentation.exception.EntityNotFoundException
import java.time.Instant

class TicketServiceTest : DescribeSpec({
    val tickerRepository = mockk<TicketRepository>()
    val ticketService = TicketService(ticketRepository = tickerRepository)

    describe("deleteTicket") {
        context("Ticket 없을 경우") {
            it("EntityNotFoundException 발생한다") {
                val ticketId = 1L

                every { tickerRepository.findByIdOrNull(ticketId) } returns null

                shouldThrow<EntityNotFoundException> { ticketService.deleteTicket(ticketId) }
            }
        }

        context("Ticket 있을 경우") {
            it("삭제한다") {
                val ticketId = 1L
                val ticket = Ticket.create(
                    name = "name",
                    description = "description",
                    price = 1000,
                    quantity = 5,
                    place = "place",
                    performanceTime = Instant.now(),
                    duration = "duration",
                    ageLimit = "ageLimit"
                )

                every { tickerRepository.findByIdOrNull(ticketId) } returns ticket
                every { tickerRepository.delete(ticket) } returns Unit

                ticketService.deleteTicket(ticketId)

                verify(exactly = 1) { tickerRepository.delete(ticket) }
            }
        }
    }
})
