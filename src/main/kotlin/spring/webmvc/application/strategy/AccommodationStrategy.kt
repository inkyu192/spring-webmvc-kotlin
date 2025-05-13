package spring.webmvc.application.strategy

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.AccommodationCreateCommand
import spring.webmvc.application.dto.command.AccommodationUpdateCommand
import spring.webmvc.application.dto.command.ProductCreateCommand
import spring.webmvc.application.dto.command.ProductUpdateCommand
import spring.webmvc.application.dto.result.AccommodationResult
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.domain.cache.AccommodationCache
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.AccommodationRepository
import spring.webmvc.presentation.exception.EntityNotFoundException

@Component
class AccommodationStrategy(
    private val accommodationCache: AccommodationCache,
    private val accommodationRepository: AccommodationRepository,
    private val objectMapper: ObjectMapper,
) : ProductStrategy {
    private val logger = LoggerFactory.getLogger(AccommodationStrategy::class.java)

    override fun supports(category: Category) = category == Category.ACCOMMODATION

    override fun findByProductId(productId: Long): ProductResult {
        val cache = accommodationCache.get(productId)
            ?.let { value ->
                runCatching { objectMapper.readValue(value, AccommodationResult::class.java) }
                    .onFailure {
                        logger.warn("Failed to deserialize cache for productId={}: {}", productId, it.message)
                    }
                    .getOrNull()
            }

        if (cache != null) {
            return cache
        }

        val accommodationResult = accommodationRepository.findByProductId(productId)
            ?.let { AccommodationResult(accommodation = it) }
            ?: throw EntityNotFoundException(kClass = AccommodationRepository::class, id = productId)

        runCatching { objectMapper.writeValueAsString(accommodationResult) }
            .onSuccess { value -> accommodationCache.set(id = productId, value = value) }
            .onFailure { logger.warn("Failed to serialize cache for productId={}: {}", productId, it.message) }

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
    }
}