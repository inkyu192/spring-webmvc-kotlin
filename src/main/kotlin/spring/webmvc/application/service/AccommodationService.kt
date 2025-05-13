package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.repository.AccommodationRepository
import spring.webmvc.presentation.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class AccommodationService(
    private val accommodationRepository: AccommodationRepository,
) {
    @Transactional
    fun deleteAccommodation(id: Long) {
        val accommodation = accommodationRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(kClass = AccommodationRepository::class, id = id)

        accommodationRepository.delete(accommodation)
    }
}
