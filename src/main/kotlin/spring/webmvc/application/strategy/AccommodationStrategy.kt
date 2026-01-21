package spring.webmvc.application.strategy

import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.AccommodationPutCommand
import spring.webmvc.application.dto.command.ProductAttributePutCommand
import spring.webmvc.application.dto.result.AccommodationResult
import spring.webmvc.application.dto.result.ProductAttributeResult
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.ProductCategory
import spring.webmvc.domain.repository.AccommodationRepository

@Component
class AccommodationStrategy(
    private val accommodationRepository: AccommodationRepository,
) : ProductAttributeStrategy {
    override fun category() = ProductCategory.ACCOMMODATION

    override fun supports(command: ProductAttributePutCommand) = command is AccommodationPutCommand

    override fun findByProductId(productId: Long): ProductAttributeResult {
        val accommodation = accommodationRepository.findByProductId(productId)

        return AccommodationResult.of(accommodation)
    }

    override fun create(product: Product, command: ProductAttributePutCommand): ProductAttributeResult {
        val accommodationCommand = command as AccommodationPutCommand

        val accommodation = Accommodation.create(
            product = product,
            place = accommodationCommand.place,
            checkInTime = accommodationCommand.checkInTime,
            checkOutTime = accommodationCommand.checkOutTime,
        )

        accommodationRepository.save(accommodation)

        return AccommodationResult.of(accommodation)
    }

    override fun update(productId: Long, command: ProductAttributePutCommand): ProductAttributeResult {
        val accommodationCommand = command as AccommodationPutCommand

        val accommodation = accommodationRepository.findByProductId(productId)

        accommodation.replace(
            place = accommodationCommand.place,
            checkInTime = accommodationCommand.checkInTime,
            checkOutTime = accommodationCommand.checkOutTime,
        )

        return AccommodationResult.of(accommodation)
    }

    override fun deleteProduct(productId: Long) {
        val accommodation = accommodationRepository.findByProductId(productId)

        accommodationRepository.delete(accommodation)
    }
}
