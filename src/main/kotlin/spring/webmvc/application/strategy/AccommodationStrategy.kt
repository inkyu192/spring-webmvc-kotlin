package spring.webmvc.application.strategy

import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.AccommodationCreateCommand
import spring.webmvc.application.dto.command.AccommodationUpdateCommand
import spring.webmvc.application.dto.command.ProductAttributeCreateCommand
import spring.webmvc.application.dto.command.ProductAttributeUpdateCommand
import spring.webmvc.application.dto.result.AccommodationResult
import spring.webmvc.application.dto.result.ProductAttributeResult
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.AccommodationRepository

@Component
class AccommodationStrategy(
    private val accommodationRepository: AccommodationRepository,
) : ProductAttributeStrategy {
    override fun category() = Category.ACCOMMODATION

    override fun findByProductId(productId: Long): ProductAttributeResult {
        val accommodation = accommodationRepository.findByProductId(productId)

        return AccommodationResult.from(accommodation)
    }

    override fun createProduct(product: Product, command: ProductAttributeCreateCommand): ProductAttributeResult {
        val accommodationCommand = command as AccommodationCreateCommand

        val accommodation = Accommodation.create(
            product = product,
            place = accommodationCommand.place,
            checkInTime = accommodationCommand.checkInTime,
            checkOutTime = accommodationCommand.checkOutTime,
        )

        accommodationRepository.save(accommodation)

        return AccommodationResult.from(accommodation)
    }

    override fun updateProduct(productId: Long, command: ProductAttributeUpdateCommand): ProductAttributeResult {
        val accommodationCommand = command as AccommodationUpdateCommand

        val accommodation = accommodationRepository.findByProductId(productId)

        accommodation.update(
            place = accommodationCommand.place,
            checkInTime = accommodationCommand.checkInTime,
            checkOutTime = accommodationCommand.checkOutTime,
        )

        return AccommodationResult.from(accommodation)
    }

    override fun deleteProduct(productId: Long) {
        val accommodation = accommodationRepository.findByProductId(productId)

        accommodationRepository.delete(accommodation)
    }
}