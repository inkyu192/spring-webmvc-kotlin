package spring.webmvc.application.strategy

import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.AccommodationCreateCommand
import spring.webmvc.application.dto.command.AccommodationUpdateCommand
import spring.webmvc.application.dto.command.ProductCreateCommand
import spring.webmvc.application.dto.command.ProductUpdateCommand
import spring.webmvc.application.dto.result.AccommodationResult
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.domain.model.cache.AccommodationCache
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.AccommodationRepository
import spring.webmvc.domain.repository.cache.AccommodationCacheRepository
import spring.webmvc.presentation.exception.EntityNotFoundException

@Component
class AccommodationStrategy(
    private val accommodationCacheRepository: AccommodationCacheRepository,
    private val accommodationRepository: AccommodationRepository,
) : ProductStrategy {
    override fun category() = Category.ACCOMMODATION

    override fun findByProductId(productId: Long): ProductResult {
        val cached = accommodationCacheRepository.getAccommodation(productId)

        if (cached != null) {
            return AccommodationResult(accommodationCache = cached)
        }

        val accommodation = accommodationRepository.findByIdOrNull(productId)
            ?: throw EntityNotFoundException(kClass = Accommodation::class, id = productId)

        accommodationCacheRepository.setAccommodation(
            productId = productId,
            accommodationCache = AccommodationCache.create(
                id = productId,
                name = accommodation.name,
                description = accommodation.description,
                price = accommodation.price,
                quantity = accommodation.quantity,
                createdAt = accommodation.createdAt,
                accommodationId = checkNotNull(accommodation.id),
                place = accommodation.place,
                checkInTime = accommodation.checkInTime,
                checkOutTime = accommodation.checkOutTime
            )
        )

        return AccommodationResult(accommodation)
    }

    override fun createProduct(productCreateCommand: ProductCreateCommand): ProductResult {
        val accommodationCreateCommand = productCreateCommand as AccommodationCreateCommand

        val accommodation = accommodationRepository.save(
            Accommodation.create(
                name = accommodationCreateCommand.name,
                description = accommodationCreateCommand.description,
                price = accommodationCreateCommand.price,
                quantity = accommodationCreateCommand.quantity,
                place = accommodationCreateCommand.place,
                checkInTime = accommodationCreateCommand.checkInTime,
                checkOutTime = accommodationCreateCommand.checkOutTime
            )
        )

        return AccommodationResult(accommodation)
    }

    override fun updateProduct(productId: Long, productUpdateCommand: ProductUpdateCommand): ProductResult {
        val accommodationUpdateCommand = productUpdateCommand as AccommodationUpdateCommand

        val accommodation = accommodationRepository.findByIdOrNull(productId)
            ?: throw EntityNotFoundException(kClass = Accommodation::class, id = productId)

        accommodation.update(
            name = accommodationUpdateCommand.name,
            description = accommodationUpdateCommand.description,
            price = accommodationUpdateCommand.price,
            quantity = accommodationUpdateCommand.quantity,
            place = accommodationUpdateCommand.place,
            checkInTime = accommodationUpdateCommand.checkInTime,
            checkOutTime = accommodationUpdateCommand.checkOutTime,
        )

        return AccommodationResult(accommodation)
    }

    override fun deleteProduct(productId: Long) {
        val accommodation = accommodationRepository.findByIdOrNull(productId)
            ?: throw EntityNotFoundException(kClass = Accommodation::class, id = productId)

        accommodationRepository.delete(accommodation)

        accommodationCacheRepository.deleteAccommodation(productId)
    }
}