package spring.webmvc.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.repository.AccommodationRepository
import spring.webmvc.presentation.exception.EntityNotFoundException
import java.time.Instant
import java.time.temporal.ChronoUnit

class AccommodationServiceTest : DescribeSpec({
    val accommodationRepository = mockk<AccommodationRepository>()
    val accommodationService = AccommodationService(accommodationRepository)

    describe("createAccommodation") {
        it("Accommodation 저장 후 반환한다") {
            val name = "name"
            val description = "description"
            val price = 1000
            val quantity = 5
            val place = "place"
            val checkInTime = Instant.now()
            val checkOutTime = Instant.now().plus(1, ChronoUnit.DAYS)

            val accommodation = Accommodation.create(
                name = name,
                description = description,
                price = price,
                quantity = quantity,
                place = place,
                checkInTime = checkInTime,
                checkOutTime = checkOutTime,
            )

            every { accommodationRepository.save(accommodation = any<Accommodation>()) } returns accommodation

            val result = accommodationService.createAccommodation(
                name = name,
                description = description,
                price = price,
                quantity = quantity,
                place = place,
                checkInTime = checkInTime,
                checkOutTime = checkOutTime,
            )

            result.product.name shouldBe name
            result.product.description shouldBe description
            result.product.price shouldBe price
            result.product.quantity shouldBe quantity
            result.place shouldBe place
            result.checkInTime shouldBe checkInTime
            result.checkOutTime shouldBe checkOutTime
        }
    }

    describe("findAccommodation") {
        context("Accommodation 없을 경우") {
            it("EntityNotFoundException 발생한다") {
                val accommodationId = 1L

                every { accommodationRepository.findByIdOrNull(accommodationId) } returns null

                shouldThrow<EntityNotFoundException> { accommodationService.findAccommodation(accommodationId) }
            }
        }

        context("Accommodation 있을 경우") {
            it("조회 후 반환한다") {
                val accommodationId = 1L
                val accommodation = Accommodation.create(
                    name = "name",
                    description = "description",
                    price = 1000,
                    quantity = 5,
                    place = "place",
                    checkInTime = Instant.now(),
                    checkOutTime = Instant.now().plus(1, ChronoUnit.DAYS),
                )

                every { accommodationRepository.findByIdOrNull(accommodationId) } returns accommodation


                val result = accommodationService.findAccommodation(accommodationId)

                result.product.name shouldBe accommodation.product.name
                result.product.description shouldBe accommodation.product.description
                result.product.price shouldBe accommodation.product.price
                result.product.quantity shouldBe accommodation.product.quantity
                result.place shouldBe accommodation.place
                result.checkInTime shouldBe accommodation.checkInTime
                result.checkOutTime shouldBe accommodation.checkOutTime
            }
        }
    }

    describe("updateAccommodation") {
        context("Accommodation 없을 경우") {
            it("EntityNotFoundException 발생한다") {
                val accommodationId = 1L
                val name = "name"
                val description = "description"
                val price = 1000
                val quantity = 5
                val place = "place"
                val checkInTime = Instant.now()
                val checkOutTime = Instant.now().plus(1, ChronoUnit.DAYS)

                every { accommodationRepository.findByIdOrNull(accommodationId) } returns null

                shouldThrow<EntityNotFoundException> {
                    accommodationService.updateAccommodation(
                        id = accommodationId,
                        name = name,
                        description = description,
                        price = price,
                        quantity = quantity,
                        place = place,
                        checkInTime = checkInTime,
                        checkOutTime = checkOutTime,
                    )
                }
            }
        }

        context("Accommodation 있을 경우") {
            it("수정 후 반환한다") {
                val accommodationId = 1L
                val name = "name"
                val description = "description"
                val price = 1000
                val quantity = 5
                val place = "place"
                val checkInTime = Instant.now()
                val checkOutTime = Instant.now().plus(1, ChronoUnit.DAYS)

                val accommodation = Accommodation.create(
                    name = name,
                    description = description,
                    price = price,
                    quantity = quantity,
                    place = place,
                    checkInTime = checkInTime,
                    checkOutTime = checkOutTime,
                )

                every { accommodationRepository.findByIdOrNull(accommodationId) } returns accommodation

                val result = accommodationService.updateAccommodation(
                    id = accommodationId,
                    name = name,
                    description = description,
                    price = price,
                    quantity = quantity,
                    place = place,
                    checkInTime = checkInTime,
                    checkOutTime = checkOutTime,
                )

                result.product.name shouldBe name
                result.product.description shouldBe description
                result.product.price shouldBe price
                result.product.quantity shouldBe quantity
                result.place shouldBe place
                result.checkInTime shouldBe checkInTime
                result.checkOutTime shouldBe checkOutTime
                result.id shouldBe accommodationId
            }
        }
    }

    describe("deleteAccommodation") {
        context("Accommodation 없을 경우") {
            it("EntityNotFoundException 발생한다") {
                val accommodationId = 1L

                every { accommodationRepository.findByIdOrNull(accommodationId) } returns null

                shouldThrow<EntityNotFoundException> { accommodationService.deleteAccommodation(accommodationId) }
            }
        }

        context("Accommodation 있을 경우") {
            it("삭제한다") {
                val accommodationId = 1L
                val accommodation = Accommodation.create(
                    name = "name",
                    description = "description",
                    price = 1000,
                    quantity = 5,
                    place = "place",
                    checkInTime = Instant.now(),
                    checkOutTime = Instant.now().plus(1, ChronoUnit.DAYS),
                )

                every { accommodationRepository.findByIdOrNull(accommodationId) } returns accommodation
                every { accommodationRepository.delete(accommodation) } returns Unit

                accommodationService.deleteAccommodation(accommodationId)

                verify(exactly = 1) { accommodationRepository.delete(accommodation) }
            }
        }
    }
})