package spring.webmvc.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import spring.webmvc.domain.model.entity.Ticket
import spring.webmvc.domain.repository.TicketRepository
import spring.webmvc.presentation.exception.EntityNotFoundException
import java.time.Instant

class TicketServiceTest : DescribeSpec({
    val tickerRepository = mockk<TicketRepository>()
    val ticketService = TicketService(tickerRepository)

    describe("createTicket") {
        it("Ticket 저장 후 반환한다") {
            val name = "name"
            val description = "description"
            val price = 1000
            val quantity = 5
            val place = "place"
            val performanceTime = Instant.now()
            val duration = "duration"
            val ageLimit = "ageLimit"

            val ticket = Ticket.create(
                name = name,
                description = description,
                price = price,
                quantity = quantity,
                place = place,
                performanceTime = performanceTime,
                duration = duration,
                ageLimit = ageLimit,
            )

            every { tickerRepository.save(any<Ticket>()) } returns ticket

            val result = ticketService.createTicket(
                name,
                description,
                price,
                quantity,
                place,
                performanceTime,
                duration,
                ageLimit
            )

            result.product.name shouldBe name
            result.product.description shouldBe description
            result.product.price shouldBe price
            result.product.quantity shouldBe quantity
            result.place shouldBe place
            result.performanceTime shouldBe performanceTime
            result.duration shouldBe duration
            result.ageLimit shouldBe ageLimit
        }
    }

    describe("findTicket") {
        context("Ticket 없을 경우") {
            it("EntityNotFoundException 발생한다") {
                val ticketId = 1L

                every { tickerRepository.findByIdOrNull(ticketId) } returns null

                shouldThrow<EntityNotFoundException> { ticketService.findTicket(ticketId) }
            }
        }

        context("Ticket 있을 경우") {
            it("조회 후 반환한다") {
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

                val result = ticketService.findTicket(ticketId)

                result.product.name shouldBe ticket.product.name
                result.product.description shouldBe ticket.product.description
                result.product.price shouldBe ticket.product.price
                result.product.quantity shouldBe ticket.product.quantity
                result.place shouldBe ticket.place
                result.performanceTime shouldBe ticket.performanceTime
                result.duration shouldBe ticket.duration
                result.ageLimit shouldBe ticket.ageLimit
            }
        }
    }

    describe("updateTicket") {
        context("Ticket 없을 경우") {
            it("EntityNotFoundException 발생한다") {
                val ticketId = 1L
                val name = "name"
                val description = "description"
                val price = 1000
                val quantity = 5
                val place = "place"
                val performanceTime = Instant.now()
                val duration = "duration"
                val ageLimit = "ageLimit"

                every { tickerRepository.findByIdOrNull(ticketId) } returns null

                shouldThrow<EntityNotFoundException> {
                    ticketService.updateTicket(
                        id = ticketId,
                        name = name,
                        description = description,
                        price = price,
                        quantity = quantity,
                        place = place,
                        performanceTime = performanceTime,
                        duration = duration,
                        ageLimit = ageLimit,
                    )
                }
            }
        }

        context("Ticket 있을 경우") {
            it("수정 후 반환한다") {
                val ticketId = 1L
                val name = "name"
                val description = "description"
                val price = 1000
                val quantity = 5
                val place = "place"
                val performanceTime = Instant.now()
                val duration = "duration"
                val ageLimit = "ageLimit"

                val ticket = Ticket.create(
                    name = "name",
                    description = "description",
                    price = 1000,
                    quantity = 5,
                    place = "place",
                    performanceTime = performanceTime,
                    duration = "duration",
                    ageLimit = "ageLimit"
                )

                every { tickerRepository.findByIdOrNull(ticketId) } returns ticket

                val result = ticketService.updateTicket(
                    id = ticketId,
                    name = name,
                    description = description,
                    price = price,
                    quantity = quantity,
                    place = place,
                    performanceTime = performanceTime,
                    duration = duration,
                    ageLimit = ageLimit,
                )

                result.product.name shouldBe ticket.product.name
                result.product.description shouldBe ticket.product.description
                result.product.price shouldBe ticket.product.price
                result.product.quantity shouldBe ticket.product.quantity
                result.place shouldBe ticket.place
                result.performanceTime shouldBe ticket.performanceTime
                result.duration shouldBe ticket.duration
                result.ageLimit shouldBe ticket.ageLimit
            }
        }
    }

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
