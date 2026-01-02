package spring.webmvc.application.strategy

import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.AccommodationPutCommand
import spring.webmvc.application.dto.command.ProductPutCommand
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.AccommodationRepository

@Component
class AccommodationStrategy(
    private val accommodationRepository: AccommodationRepository,
) : ProductStrategy {
    override fun category() = Category.ACCOMMODATION

    override fun findByProductId(productId: Long): ProductResult {
        val accommodation = accommodationRepository.findById(productId)

        return ProductResult.from(accommodation)
    }

    override fun createProduct(product: Product, command: ProductPutCommand): ProductResult {
        val accommodationCommand = command.detail as AccommodationPutCommand

        val accommodation = accommodationRepository.save(
            Accommodation.create(
                product = product,
                place = accommodationCommand.place,
                checkInTime = accommodationCommand.checkInTime,
                checkOutTime = accommodationCommand.checkOutTime,
            )
        )

        return ProductResult.from(accommodation)
    }

    override fun updateProduct(productId: Long, command: ProductPutCommand): ProductResult {
        val accommodationCommand = command.detail as AccommodationPutCommand

        val accommodation = accommodationRepository.findById(productId)

        accommodation.update(
            place = accommodationCommand.place,
            checkInTime = accommodationCommand.checkInTime,
            checkOutTime = accommodationCommand.checkOutTime,
        )

        return ProductResult.from(accommodation)
    }

    override fun deleteProduct(productId: Long) {
        val accommodation = accommodationRepository.findById(productId)

        accommodationRepository.delete(accommodation)
    }
}