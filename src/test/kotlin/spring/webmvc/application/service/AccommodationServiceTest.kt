package spring.webmvc.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.springframework.http.HttpStatus
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.repository.AccommodationRepository
import spring.webmvc.presentation.dto.request.AccommodationCreateRequest
import spring.webmvc.presentation.dto.request.AccommodationUpdateRequest
import spring.webmvc.presentation.exception.EntityNotFoundException
import java.time.Instant
import java.time.temporal.ChronoUnit

class AccommodationServiceTest : DescribeSpec({
    val accommodationRepository = mockk<AccommodationRepository>()
    val accommodationService = AccommodationService(accommodationRepository)

    describe("createAccommodation") {
        it("Accommodation 저장 후 반환한다") {
            val checkInTime = Instant.now()
            val checkOutTime = Instant.now().plus(1, ChronoUnit.DAYS)
            val request = AccommodationCreateRequest(
                name = "name",
                description = "description",
                price = 1000,
                quantity = 5,
                place = "place",
                checkInTime = checkInTime,
                checkOutTime = checkOutTime,
            )
            val accommodation = spyk(
                Accommodation.create(
                    name = "name",
                    description = "description",
                    price = 1000,
                    quantity = 5,
                    place = "place",
                    checkInTime = checkInTime,
                    checkOutTime = checkOutTime,
                )
            ).apply { every { id } returns 1L }

            every { accommodationRepository.save(any<Accommodation>()) } returns accommodation

            accommodationService.createAccommodation(request).apply {
                name shouldBe request.name
                description shouldBe request.description
                price shouldBe request.price
                quantity shouldBe request.quantity
                place shouldBe request.place
                checkInTime shouldBe request.checkInTime
                checkOutTime shouldBe request.checkOutTime
            }
        }
    }

    describe("findAccommodation") {
        context("Accommodation 없을 경우") {
            it("EntityNotFoundException 발생한다") {
                val accommodationId = 1L

                every { accommodationRepository.findByIdOrNull(accommodationId) } returns null

                shouldThrow<EntityNotFoundException> { accommodationService.findAccommodation(accommodationId) }.apply {
                    httpStatus shouldBe HttpStatus.NOT_FOUND
                }
            }
        }

        context("Accommodation 있을 경우") {
            it("조회 후 반환한다") {
                val accommodationId = 1L
                val checkInTime = Instant.now()
                val checkOutTime = checkInTime.plus(1, ChronoUnit.DAYS)
                val accommodation = spyk(
                    Accommodation.create(
                        name = "name",
                        description = "description",
                        price = 1000,
                        quantity = 5,
                        place = "place",
                        checkInTime = checkInTime,
                        checkOutTime = checkOutTime,
                    )
                ).apply { every { id } returns accommodationId }

                every { accommodationRepository.findByIdOrNull(accommodationId) } returns accommodation

                accommodationService.findAccommodation(accommodationId).apply {
                    id shouldBe accommodationId
                }
            }
        }
    }

    describe("updateAccommodation") {
        context("Accommodation 없을 경우") {
            it("EntityNotFoundException 발생한다") {
                val accommodationId = 1L
                val checkInTime = Instant.now()
                val checkOutTime = checkInTime.plus(1, ChronoUnit.DAYS)
                val request = AccommodationUpdateRequest(
                    name = "name",
                    description = "description",
                    price = 1000,
                    quantity = 5,
                    place = "place",
                    checkInTime = checkInTime,
                    checkOutTime = checkOutTime,
                )

                every { accommodationRepository.findByIdOrNull(accommodationId) } returns null

                shouldThrow<EntityNotFoundException> {
                    accommodationService.updateAccommodation(
                        id = accommodationId,
                        accommodationUpdateRequest = request
                    )
                }.apply {
                    httpStatus shouldBe HttpStatus.NOT_FOUND
                }
            }
        }

        context("Accommodation 있을 경우") {
            it("수정 후 반환한다") {
                val accommodationId = 1L
                val checkInTime = Instant.now()
                val checkOutTime = checkInTime.plus(1, ChronoUnit.DAYS)
                val request = AccommodationUpdateRequest(
                    name = "name",
                    description = "description",
                    price = 1000,
                    quantity = 5,
                    place = "place",
                    checkInTime = checkInTime,
                    checkOutTime = checkOutTime,
                )
                val accommodation = spyk(
                    Accommodation.create(
                        name = "name",
                        description = "description",
                        price = 1000,
                        quantity = 5,
                        place = "place",
                        checkInTime = checkInTime,
                        checkOutTime = checkOutTime,
                    )
                ).apply { every { id } returns accommodationId }

                every { accommodationRepository.findByIdOrNull(accommodationId) } returns accommodation

                accommodationService.updateAccommodation(id = accommodationId, accommodationUpdateRequest = request)
                    .apply {
                        name shouldBe request.name
                        description shouldBe request.description
                        price shouldBe request.price
                        quantity shouldBe request.quantity
                        place shouldBe request.place
                        checkInTime shouldBe request.checkInTime
                        checkOutTime shouldBe request.checkOutTime
                        id shouldBe accommodationId
                    }
            }
        }
    }

    describe("deleteAccommodation") {
        context("Accommodation 없을 경우") {
            it("EntityNotFoundException 발생한다") {
                val accommodationId = 1L

                every { accommodationRepository.findByIdOrNull(accommodationId) } returns null

                shouldThrow<EntityNotFoundException> { accommodationService.deleteAccommodation(accommodationId) }.apply {
                    httpStatus shouldBe HttpStatus.NOT_FOUND
                }
            }
        }

        context("Accommodation 있을 경우") {
            it("삭제한다") {
                val accommodationId = 1L
                val checkInTime = Instant.now()
                val checkOutTime = checkInTime.plus(1, ChronoUnit.DAYS)
                val accommodation = spyk(
                    Accommodation.create(
                        name = "name",
                        description = "description",
                        price = 1000,
                        quantity = 5,
                        place = "place",
                        checkInTime = checkInTime,
                        checkOutTime = checkOutTime,
                    )
                ).apply { every { id } returns accommodationId }

                every { accommodationRepository.findByIdOrNull(accommodationId) } returns accommodation
                every { accommodationRepository.delete(accommodation) } returns Unit

                accommodationService.deleteAccommodation(accommodationId)

                verify(exactly = 1) { accommodationRepository.delete(accommodation) }
            }
        }
    }
})