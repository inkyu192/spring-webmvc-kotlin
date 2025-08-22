package spring.webmvc.application.strategy

import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.AccommodationCreateCommand
import spring.webmvc.application.dto.command.AccommodationUpdateCommand
import spring.webmvc.application.dto.command.ProductCreateCommand
import spring.webmvc.application.dto.command.ProductUpdateCommand
import spring.webmvc.application.dto.result.AccommodationResult
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.cache.AccommodationCacheRepository
import spring.webmvc.domain.repository.AccommodationRepository
import spring.webmvc.presentation.exception.EntityNotFoundException

@Component
class AccommodationStrategy(
    private val accommodationCacheRepository: AccommodationCacheRepository,
    private val accommodationRepository: AccommodationRepository,
) : ProductStrategy {
    override fun category() = Category.ACCOMMODATION

    override fun findByProductId(productId: Long): ProductResult {
        val cache = accommodationCacheRepository.getAccommodation(productId)

        if (cache != null) {
            return cache
        }

        val accommodationResult = accommodationRepository.findByProductId(productId)
            ?.let { AccommodationResult(accommodation = it) }
            ?: throw EntityNotFoundException(kClass = AccommodationRepository::class, id = productId)

        accommodationCacheRepository.setAccommodation(productId, accommodationResult)

        return accommodationResult
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

        val accommodation = accommodationRepository.findByProductId(productId)
            ?: throw EntityNotFoundException(kClass = AccommodationRepository::class, id = productId)

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
        val accommodation = accommodationRepository.findByProductId(productId)
            ?: throw EntityNotFoundException(kClass = AccommodationRepository::class, id = productId)

        accommodationRepository.delete(accommodation)

        accommodationCacheRepository.deleteAccommodation(productId)
    }
}