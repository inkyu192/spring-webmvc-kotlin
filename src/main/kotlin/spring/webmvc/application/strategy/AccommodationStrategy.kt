package spring.webmvc.application.strategy

import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.AccommodationPutCommand
import spring.webmvc.application.dto.command.ProductPropertyPutCommand
import spring.webmvc.application.dto.result.AccommodationResult
import spring.webmvc.application.dto.result.ProductPropertyResult
import spring.webmvc.domain.model.entity.Accommodation
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.ProductCategory
import spring.webmvc.domain.repository.AccommodationRepository

@Component
class AccommodationStrategy(
    private val accommodationRepository: AccommodationRepository,
) : ProductPropertyStrategy {
    override fun category() = ProductCategory.ACCOMMODATION

    override fun findByProductId(productId: Long): ProductPropertyResult {
        val accommodation = accommodationRepository.findByProductId(productId)

        return AccommodationResult.from(accommodation)
    }

    override fun create(product: Product, command: ProductPropertyPutCommand): ProductPropertyResult {
        val accommodationCommand = command as AccommodationPutCommand

        val accommodation = Accommodation.create(
            product = product,
            place = accommodationCommand.place,
            checkInTime = accommodationCommand.checkInTime,
            checkOutTime = accommodationCommand.checkOutTime,
        )

        accommodationRepository.save(accommodation)

        return AccommodationResult.from(accommodation)
    }

    override fun replace(productId: Long, command: ProductPropertyPutCommand): ProductPropertyResult {
        val accommodationCommand = command as AccommodationPutCommand

        val accommodation = accommodationRepository.findByProductId(productId)

        accommodation.replace(
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