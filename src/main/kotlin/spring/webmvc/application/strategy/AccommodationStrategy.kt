package spring.webmvc.application.strategy

import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.AccommodationCreateCommand
import spring.webmvc.application.dto.command.AccommodationUpdateCommand
import spring.webmvc.application.dto.command.ProductCreateCommand
import spring.webmvc.application.dto.command.ProductUpdateCommand
import spring.webmvc.application.dto.result.AccommodationResult
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.domain.cache.CacheKey
import spring.webmvc.domain.cache.ValueCache
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.AccommodationRepository
import spring.webmvc.presentation.exception.EntityNotFoundException

@Component
class AccommodationStrategy(
    private val valueCache: ValueCache,
    private val accommodationRepository: AccommodationRepository,
) : ProductStrategy {
    override fun supports(category: Category) = category == Category.ACCOMMODATION

    override fun findByProductId(productId: Long): ProductResult {
        val key = CacheKey.PRODUCT.generate(productId)
        val cache = valueCache.get(key = key, clazz = AccommodationResult::class.java)

        if (cache != null) {
            return cache
        }

        val accommodationResult = accommodationRepository.findByProductId(productId)
            ?.let { AccommodationResult(accommodation = it) }
            ?: throw EntityNotFoundException(kClass = AccommodationRepository::class, id = productId)

        valueCache.set(key = key, value = accommodationResult, timeout = CacheKey.PRODUCT.timeOut)

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

        val key = CacheKey.PRODUCT_STOCK.generate(checkNotNull(accommodation.product.id))
        valueCache.set(key = key, value = accommodation.product.quantity)

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

        val key = CacheKey.PRODUCT_STOCK.generate(productId)
        valueCache.set(key = key, value = accommodation.product.quantity)

        return AccommodationResult(accommodation)
    }

    override fun deleteProduct(productId: Long) {
        val accommodation = accommodationRepository.findByProductId(productId)
            ?: throw EntityNotFoundException(kClass = AccommodationRepository::class, id = productId)

        val key = CacheKey.PRODUCT_STOCK.generate(productId)
        valueCache.delete(key = key)

        accommodationRepository.delete(accommodation)
    }
}