package spring.webmvc.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.springframework.http.HttpStatus
import spring.webmvc.domain.model.entity.Flight
import spring.webmvc.domain.repository.FlightRepository
import spring.webmvc.presentation.dto.request.FlightCreateRequest
import spring.webmvc.presentation.dto.request.FlightUpdateRequest
import spring.webmvc.presentation.exception.EntityNotFoundException
import java.time.Instant
import java.time.temporal.ChronoUnit

class FlightServiceTest : DescribeSpec({
    val flightRepository = mockk<FlightRepository>()
    val flightService = FlightService(flightRepository)

    describe("createFlight") {
        it("Flight 저장 후 반환한다") {
            val departureTime = Instant.now()
            val arrivalTime = departureTime.plus(1, ChronoUnit.DAYS)
            val request = FlightCreateRequest(
                name = "name",
                description = "description",
                price = 1000,
                quantity = 5,
                airline = "airline",
                flightNumber = "flightNumber",
                departureAirport = "departureAirport",
                arrivalAirport = "arrivalAirport",
                departureTime = departureTime,
                arrivalTime = arrivalTime,
            )
            val flight = spyk(
                Flight.create(
                    name = "name",
                    description = "description",
                    price = 1000,
                    quantity = 5,
                    airline = "airline",
                    flightNumber = "flightNumber",
                    departureAirport = "departureAirport",
                    arrivalAirport = "arrivalAirport",
                    departureTime = departureTime,
                    arrivalTime = arrivalTime,
                )
            ).apply { every { id } returns 1L }

            every { flightRepository.save(any<Flight>()) } returns flight

            flightService.createFlight(request).apply {
                name shouldBe request.name
                description shouldBe request.description
                price shouldBe request.price
                quantity shouldBe request.quantity
                departureAirport shouldBe request.departureAirport
                arrivalAirport shouldBe request.arrivalAirport
                departureTime shouldBe request.departureTime
                arrivalTime shouldBe request.arrivalTime
                airline shouldBe request.airline
            }
        }
    }

    describe("findFlight") {
        context("Flight 없을 경우") {
            it("EntityNotFoundException 발생한다") {
                val flightId = 1L

                every { flightRepository.findByIdOrNull(flightId) } returns null

                shouldThrow<EntityNotFoundException> { flightService.findFlight(flightId) }.apply {
                    httpStatus shouldBe HttpStatus.NOT_FOUND
                }
            }
        }

        context("Flight 있을 경우") {
            it("조회 후 반환한다") {
                val flightId = 1L
                val departureTime = Instant.now()
                val arrivalTime = departureTime.plus(1, ChronoUnit.DAYS)
                val flight = spyk(
                    Flight.create(
                        name = "name",
                        description = "description",
                        price = 1000,
                        quantity = 5,
                        airline = "airline",
                        flightNumber = "flightNumber",
                        departureAirport = "departureAirport",
                        arrivalAirport = "arrivalAirport",
                        departureTime = departureTime,
                        arrivalTime = arrivalTime,
                    )
                ).apply { every { id } returns flightId }

                every { flightRepository.findByIdOrNull(flightId) } returns flight

                flightService.findFlight(flightId).apply {
                    id shouldBe flightId
                }
            }
        }
    }

    describe("updateFlight") {
        context("Flight 없을 경우") {
            it("EntityNotFoundException 발생한다") {
                val flightId = 1L
                val departureTime = Instant.now()
                val arrivalTime = departureTime.plus(1, ChronoUnit.DAYS)
                val request = FlightUpdateRequest(
                    name = "name",
                    description = "description",
                    price = 1000,
                    quantity = 5,
                    airline = "airline",
                    flightNumber = "flightNumber",
                    departureAirport = "departureAirport",
                    arrivalAirport = "arrivalAirport",
                    departureTime = departureTime,
                    arrivalTime = arrivalTime,
                )

                every { flightRepository.findByIdOrNull(flightId) } returns null

                shouldThrow<EntityNotFoundException> {
                    flightService.updateFlight(
                        id = flightId,
                        flightUpdateRequest = request
                    )
                }.apply {
                    httpStatus shouldBe HttpStatus.NOT_FOUND
                }
            }
        }

        context("Flight 있을 경우") {
            it("수정 후 반환한다") {
                val flightId = 1L
                val departureTime = Instant.now()
                val arrivalTime = departureTime.plus(1, ChronoUnit.DAYS)
                val request = FlightUpdateRequest(
                    name = "name",
                    description = "description",
                    price = 1000,
                    quantity = 5,
                    airline = "airline",
                    flightNumber = "flightNumber",
                    departureAirport = "departureAirport",
                    arrivalAirport = "arrivalAirport",
                    departureTime = departureTime,
                    arrivalTime = arrivalTime,
                )
                val flight = spyk(
                    Flight.create(
                        name = "name",
                        description = "description",
                        price = 1000,
                        quantity = 5,
                        airline = "airline",
                        flightNumber = "flightNumber",
                        departureAirport = "departureAirport",
                        arrivalAirport = "arrivalAirport",
                        departureTime = departureTime,
                        arrivalTime = arrivalTime,
                    )
                ).apply { every { id } returns flightId }

                every { flightRepository.findByIdOrNull(flightId) } returns flight

                flightService.updateFlight(id = flightId, flightUpdateRequest = request).apply {
                    name shouldBe request.name
                    description shouldBe request.description
                    price shouldBe request.price
                    quantity shouldBe request.quantity
                    departureAirport shouldBe request.departureAirport
                    arrivalAirport shouldBe request.arrivalAirport
                    departureTime shouldBe request.departureTime
                    arrivalTime shouldBe request.arrivalTime
                    airline shouldBe request.airline
                    id shouldBe flightId
                }
            }
        }
    }

    describe("deleteFlight") {
        context("Flight 없을 경우") {
            it("EntityNotFoundException 발생한다") {
                val flightId = 1L

                every { flightRepository.findByIdOrNull(flightId) } returns null

                shouldThrow<EntityNotFoundException> { flightService.deleteFlight(flightId) }.apply {
                    httpStatus shouldBe HttpStatus.NOT_FOUND
                }
            }
        }

        context("Flight 있을 경우") {
            it("삭제한다") {
                val flightId = 1L
                val departureTime = Instant.now()
                val arrivalTime = departureTime.plus(1, ChronoUnit.DAYS)
                val flight = spyk(
                    Flight.create(
                        name = "name",
                        description = "description",
                        price = 1000,
                        quantity = 5,
                        airline = "airline",
                        flightNumber = "flightNumber",
                        departureAirport = "departureAirport",
                        arrivalAirport = "arrivalAirport",
                        departureTime = departureTime,
                        arrivalTime = arrivalTime,
                    )
                ).apply { every { id } returns flightId }

                every { flightRepository.findByIdOrNull(flightId) } returns flight
                every { flightRepository.delete(flight) } returns Unit

                flightService.deleteFlight(flightId)

                verify(exactly = 1) { flightRepository.delete(flight) }
            }
        }
    }
})