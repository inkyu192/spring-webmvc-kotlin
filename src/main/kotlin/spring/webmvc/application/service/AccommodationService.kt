package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.repository.AccommodationRepository
import spring.webmvc.presentation.dto.request.AccommodationCreateRequest
import spring.webmvc.presentation.dto.request.AccommodationUpdateRequest
import spring.webmvc.presentation.dto.response.AccommodationResponse
import spring.webmvc.presentation.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class AccommodationService(
    private val accommodationRepository: AccommodationRepository,
) {
    fun findAccommodation(id: Long): AccommodationResponse {
        val accommodation = accommodationRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(clazz = AccommodationRepository::class.java, id = id)

        return AccommodationResponse(accommodation = accommodation)
    }

    @Transactional
    fun createAccommodation(accommodationCreateRequest: AccommodationCreateRequest): AccommodationResponse {
        val accommodation = accommodationRepository.save(
            Accommodation.create(
                name = accommodationCreateRequest.name,
                description = accommodationCreateRequest.description,
                price = accommodationCreateRequest.price,
                quantity = accommodationCreateRequest.quantity,
                place = accommodationCreateRequest.place,
                checkInTime = accommodationCreateRequest.checkInTime,
                checkOutTime = accommodationCreateRequest.checkOutTime,
            )
        )

        return AccommodationResponse(accommodation = accommodation)
    }

    @Transactional
    fun updateAccommodation(id: Long, accommodationUpdateRequest: AccommodationUpdateRequest): AccommodationResponse {
        val accommodation = accommodationRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(clazz = AccommodationRepository::class.java, id = id)

        accommodation.update(
            name = accommodationUpdateRequest.name,
            description = accommodationUpdateRequest.description,
            price = accommodationUpdateRequest.price,
            quantity = accommodationUpdateRequest.quantity,
            place = accommodationUpdateRequest.place,
            checkInTime = accommodationUpdateRequest.checkInTime,
            checkOutTime = accommodationUpdateRequest.checkOutTime,
        )

        return AccommodationResponse(accommodation = accommodation)
    }

    @Transactional
    fun deleteAccommodation(id: Long) {
        val accommodation = accommodationRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(clazz = AccommodationRepository::class.java, id = id)

        accommodationRepository.delete(accommodation)
    }
}