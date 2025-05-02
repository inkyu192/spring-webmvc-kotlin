package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.repository.AccommodationRepository
import spring.webmvc.presentation.exception.EntityNotFoundException
import java.time.Instant

@Service
@Transactional(readOnly = true)
class AccommodationService(
    private val accommodationRepository: AccommodationRepository,
) {
    fun findAccommodation(id: Long) = accommodationRepository.findByIdOrNull(id)
        ?: throw EntityNotFoundException(kClass = AccommodationRepository::class, id = id)

    @Transactional
    fun createAccommodation(
        name: String,
        description: String,
        price: Int,
        quantity: Int,
        place: String,
        checkInTime: Instant,
        checkOutTime: Instant,
    ) = accommodationRepository.save(
        Accommodation.create(
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            place = place,
            checkInTime = checkInTime,
            checkOutTime = checkOutTime,
        )
    )

    @Transactional
    fun updateAccommodation(
        id: Long,
        name: String,
        description: String,
        price: Int,
        quantity: Int,
        place: String,
        checkInTime: Instant,
        checkOutTime: Instant,
    ): Accommodation {
        val accommodation = accommodationRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(kClass = AccommodationRepository::class, id = id)

        accommodation.update(
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            place = place,
            checkInTime = checkInTime,
            checkOutTime = checkOutTime,
        )

        return accommodation
    }

    @Transactional
    fun deleteAccommodation(id: Long) {
        val accommodation = accommodationRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(kClass = AccommodationRepository::class, id = id)

        accommodationRepository.delete(accommodation)
    }
}