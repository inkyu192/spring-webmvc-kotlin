package spring.webmvc.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import spring.webmvc.domain.model.entity.Flight
import spring.webmvc.domain.repository.FlightRepository
import spring.webmvc.presentation.exception.EntityNotFoundException
import java.time.Instant
import java.time.temporal.ChronoUnit

class FlightServiceTest : DescribeSpec({
    val flightRepository = mockk<FlightRepository>()
    val flightService = FlightService(flightRepository = flightRepository)

    describe("deleteFlight") {
        context("Flight 없을 경우") {
            it("EntityNotFoundException 발생한다") {
                val flightId = 1L

                every { flightRepository.findByIdOrNull(flightId) } returns null

                shouldThrow<EntityNotFoundException> { flightService.deleteFlight(flightId) }
            }
        }

        context("Flight 있을 경우") {
            it("삭제한다") {
                val flightId = 1L
                val flight = Flight.create(
                    name = "name",
                    description = "description",
                    price = 1000,
                    quantity = 5,
                    airline = "airline",
                    flightNumber = "flightNumber",
                    departureAirport = "departureAirport",
                    arrivalAirport = "arrivalAirport",
                    departureTime = Instant.now(),
                    arrivalTime = Instant.now().plus(1, ChronoUnit.DAYS),
                )

                every { flightRepository.findByIdOrNull(flightId) } returns flight
                every { flightRepository.delete(flight) } returns Unit

                flightService.deleteFlight(flightId)

                verify(exactly = 1) { flightRepository.delete(flight) }
            }
        }
    }
})
