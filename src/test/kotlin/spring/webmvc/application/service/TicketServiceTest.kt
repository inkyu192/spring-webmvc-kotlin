package spring.webmvc.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.springframework.http.HttpStatus
import spring.webmvc.domain.model.entity.Ticket
import spring.webmvc.domain.repository.TicketRepository
import spring.webmvc.presentation.dto.request.TicketCreateRequest
import spring.webmvc.presentation.dto.request.TicketUpdateRequest
import spring.webmvc.presentation.exception.EntityNotFoundException
import java.time.Instant

class TicketServiceTest : DescribeSpec({
    val tickerRepository = mockk<TicketRepository>()
    val ticketService = TicketService(tickerRepository)

    describe("createTicket") {
        it("Ticket 저장 후 반환한다") {
            val performanceTime = Instant.now()
            val request = TicketCreateRequest(
                name = "name",
                description = "description",
                price = 1000,
                quantity = 5,
                place = "place",
                performanceTime = performanceTime,
                duration = "duration",
                ageLimit = "ageLimit",
            )
            val ticket = spyk(
                Ticket.create(
                    name = "name",
                    description = "description",
                    price = 1000,
                    quantity = 5,
                    place = "place",
                    performanceTime = Instant.now(),
                    duration = "duration",
                    ageLimit = "ageLimit"
                )
            ).apply { every { id } returns 1L }

            every { tickerRepository.save(any<Ticket>()) } returns ticket

            ticketService.createTicket(request).apply {
                name shouldBe request.name
                description shouldBe request.description
                price shouldBe request.price
                quantity shouldBe request.quantity
                place shouldBe request.place
                performanceTime shouldBe request.performanceTime
                duration shouldBe request.duration
                ageLimit shouldBe request.ageLimit
            }
        }
    }

    describe("findTicket") {
        context("Ticket 없을 경우") {
            it("EntityNotFoundException 발생한다") {
                val ticketId = 1L

                every { tickerRepository.findByIdOrNull(ticketId) } returns null

                shouldThrow<EntityNotFoundException> { ticketService.findTicket(ticketId) }.apply {
                    httpStatus shouldBe HttpStatus.NOT_FOUND
                }
            }
        }

        context("Ticket 있을 경우") {
            it("조회 후 반환한다") {
                val ticketId = 1L
                val ticket = spyk(
                    Ticket.create(
                        name = "name",
                        description = "description",
                        price = 1000,
                        quantity = 5,
                        place = "place",
                        performanceTime = Instant.now(),
                        duration = "duration",
                        ageLimit = "ageLimit"
                    )
                ).apply { every { id } returns ticketId }

                every { tickerRepository.findByIdOrNull(ticketId) } returns ticket

                ticketService.findTicket(ticketId).apply {
                    id shouldBe ticketId
                }
            }
        }
    }

    describe("updateTicket") {
        context("Ticket 없을 경우") {
            it("EntityNotFoundException 발생한다") {
                val ticketId = 1L
                val request = TicketUpdateRequest(
                    name = "name",
                    description = "description",
                    price = 1000,
                    quantity = 5,
                    place = "place",
                    performanceTime = Instant.now(),
                    duration = "duration",
                    ageLimit = "ageLimit",
                )

                every { tickerRepository.findByIdOrNull(ticketId) } returns null

                shouldThrow<EntityNotFoundException> {
                    ticketService.updateTicket(
                        id = ticketId,
                        ticketUpdateRequest = request
                    )
                }.apply {
                    httpStatus shouldBe HttpStatus.NOT_FOUND
                }
            }
        }

        context("Ticket 있을 경우") {
            it("수정 후 반환한다") {
                val ticketId = 1L
                val performanceTime = Instant.now()
                val request = TicketUpdateRequest(
                    name = "name",
                    description = "description",
                    price = 1000,
                    quantity = 5,
                    place = "place",
                    performanceTime = performanceTime,
                    duration = "duration",
                    ageLimit = "ageLimit",
                )
                val ticket = spyk(
                    Ticket.create(
                        name = "name",
                        description = "description",
                        price = 1000,
                        quantity = 5,
                        place = "place",
                        performanceTime = performanceTime,
                        duration = "duration",
                        ageLimit = "ageLimit"
                    )
                ).apply { every { id } returns ticketId }

                every { tickerRepository.findByIdOrNull(ticketId) } returns ticket

                ticketService.updateTicket(id = ticketId, ticketUpdateRequest = request).apply {
                    name shouldBe request.name
                    description shouldBe request.description
                    price shouldBe request.price
                    quantity shouldBe request.quantity
                    place shouldBe request.place
                    performanceTime shouldBe request.performanceTime
                    duration shouldBe request.duration
                    ageLimit shouldBe request.ageLimit
                    id shouldBe ticketId
                }
            }
        }
    }

    describe("deleteTicket") {
        context("Ticket 없을 경우") {
            val ticketId = 1L

            every { tickerRepository.findByIdOrNull(ticketId) } returns null

            shouldThrow<EntityNotFoundException> { ticketService.deleteTicket(ticketId) }.apply {
               httpStatus shouldBe HttpStatus.NOT_FOUND
            }
        }

        context("Ticket 있을 경우") {
            val ticketId = 1L
            val ticket = spyk(
                Ticket.create(
                    name = "name",
                    description = "description",
                    price = 1000,
                    quantity = 5,
                    place = "place",
                    performanceTime = Instant.now(),
                    duration = "duration",
                    ageLimit = "ageLimit"
                )
            ).apply { every { id } returns ticketId }

            every { tickerRepository.findByIdOrNull(ticketId) } returns ticket
            every { tickerRepository.delete(ticket) } returns Unit

            ticketService.deleteTicket(ticketId)

            verify(exactly = 1) { tickerRepository.delete(ticket) }
        }
    }
})
