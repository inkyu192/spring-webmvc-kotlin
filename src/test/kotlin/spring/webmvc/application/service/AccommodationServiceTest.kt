package spring.webmvc.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
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
    val accommodationService = AccommodationService(accommodationRepository = accommodationRepository)

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