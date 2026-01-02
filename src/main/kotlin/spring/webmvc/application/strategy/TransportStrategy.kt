package spring.webmvc.application.strategy

import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.ProductPutCommand
import spring.webmvc.application.dto.command.TransportPutCommand
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.Transport
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.TransportRepository

@Component
class TransportStrategy(
    private val transportRepository: TransportRepository,
) : ProductStrategy {
    override fun category() = Category.TRANSPORT

    override fun findByProductId(productId: Long): ProductResult {
        val transport = transportRepository.findById(productId)

        return ProductResult.from(transport)
    }

    override fun createProduct(product: Product, command: ProductPutCommand): ProductResult {
        val transportCommand = command.detail as TransportPutCommand

        val transport = transportRepository.save(
            Transport.create(
                product = product,
                departureLocation = transportCommand.departureLocation,
                arrivalLocation = transportCommand.arrivalLocation,
                departureTime = transportCommand.departureTime,
                arrivalTime = transportCommand.arrivalTime,
            )
        )

        return ProductResult.from(transport)
    }

    override fun updateProduct(productId: Long, command: ProductPutCommand): ProductResult {
        val transportCommand = command.detail as TransportPutCommand

        val transport = transportRepository.findById(productId)

        transport.update(
            departureLocation = transportCommand.departureLocation,
            arrivalLocation = transportCommand.arrivalLocation,
            departureTime = transportCommand.departureTime,
            arrivalTime = transportCommand.arrivalTime,
        )

        return ProductResult.from(transport)
    }

    override fun deleteProduct(productId: Long) {
        val transport = transportRepository.findById(productId)

        transportRepository.delete(transport)
    }
}