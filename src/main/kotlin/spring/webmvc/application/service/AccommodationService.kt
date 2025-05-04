package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.AccommodationDto
import spring.webmvc.domain.cache.AccommodationCache
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.repository.AccommodationRepository
import spring.webmvc.infrastructure.common.JsonSupport
import spring.webmvc.presentation.exception.EntityNotFoundException
import java.time.Instant

@Service
@Transactional(readOnly = true)
class AccommodationService(
    private val accommodationRepository: AccommodationRepository,
    private val accommodationCache: AccommodationCache,
    private val jsonSupport: JsonSupport,
) {
    fun findAccommodation(id: Long): AccommodationDto {
        val cached = accommodationCache.get(id)
            ?.let { jsonSupport.readValue(it, AccommodationDto::class.java) }

        if (cached != null) {
            return cached
        }

        val accommodationDto = accommodationRepository.findByIdOrNull(id)
            ?.let { AccommodationDto(accommodation = it) }
            ?: throw EntityNotFoundException(kClass = AccommodationRepository::class, id = id)

        jsonSupport.writeValueAsString(obj = accommodationDto)?.let { accommodationCache.set(id = id, value = it) }

        return accommodationDto
    }

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
