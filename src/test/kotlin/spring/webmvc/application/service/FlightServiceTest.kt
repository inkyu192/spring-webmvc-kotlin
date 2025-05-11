package spring.webmvc.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import spring.webmvc.domain.cache.FlightCache
import spring.webmvc.domain.model.entity.Flight
import spring.webmvc.domain.repository.FlightRepository
import spring.webmvc.presentation.exception.EntityNotFoundException
import java.time.Instant
import java.time.temporal.ChronoUnit

class FlightServiceTest : DescribeSpec({
    val flightRepository = mockk<FlightRepository>()
    val flightService = FlightService(flightRepository = flightRepository)

    describe("updateFlight") {
        context("Flight 없을 경우") {
            it("EntityNotFoundException 발생한다") {
                val flightId = 1L
                val name = "name"
                val description = "description"
                val price = 1000
                val quantity = 5
                val airline = "airline"
                val flightNumber = "flightNumber"
                val departureAirport = "departureAirport"
                val arrivalAirport = "arrivalAirport"
                val departureTime = Instant.now()
                val arrivalTime = Instant.now().plus(1, ChronoUnit.HOURS)

                every { flightRepository.findByIdOrNull(flightId) } returns null

                shouldThrow<EntityNotFoundException> {
                    flightService.updateFlight(
                        id = flightId,
                        name = name,
                        description = description,
                        price = price,
                        quantity = quantity,
                        airline = airline,
                        flightNumber = flightNumber,
                        departureAirport = departureAirport,
                        arrivalAirport = arrivalAirport,
                        departureTime = departureTime,
                        arrivalTime = arrivalTime,
                    )
                }
            }
        }

        context("Flight 있을 경우") {
            it("수정 후 반환한다") {
                val flightId = 1L
                val name = "name"
                val description = "description"
                val price = 1000
                val quantity = 5
                val airline = "airline"
                val flightNumber = "flightNumber"
                val departureAirport = "departureAirport"
                val arrivalAirport = "arrivalAirport"
                val departureTime = Instant.now()
                val arrivalTime = Instant.now().plus(1, ChronoUnit.HOURS)

                val flight = Flight.create(
                    name = name,
                    description = description,
                    price = price,
                    quantity = quantity,
                    airline = airline,
                    flightNumber = flightNumber,
                    departureAirport = departureAirport,
                    arrivalAirport = arrivalAirport,
                    departureTime = departureTime,
                    arrivalTime = arrivalTime,
                )

                every { flightRepository.findByIdOrNull(flightId) } returns flight

                val result = flightService.updateFlight(
                    id = flightId,
                    name = name,
                    description = description,
                    price = price,
                    quantity = quantity,
                    airline = airline,
                    flightNumber = flightNumber,
                    departureAirport = departureAirport,
                    arrivalAirport = arrivalAirport,
                    departureTime = departureTime,
                    arrivalTime = arrivalTime,
                )

                result.product.name shouldBe flight.product.name
                result.product.description shouldBe flight.product.description
                result.product.price shouldBe flight.product.price
                result.product.quantity shouldBe flight.product.quantity
                result.departureAirport shouldBe flight.departureAirport
                result.arrivalAirport shouldBe flight.arrivalAirport
                result.departureTime shouldBe flight.departureTime
                result.arrivalTime shouldBe flight.arrivalTime
                result.airline shouldBe flight.airline
            }
        }
    }

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
