package spring.webmvc.application.strategy.product

import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.ProductAttributePutCommand
import spring.webmvc.application.dto.command.TransportPutCommand
import spring.webmvc.application.dto.result.ProductAttributeResult
import spring.webmvc.application.dto.result.TransportResult
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.Transport
import spring.webmvc.domain.model.enums.ProductCategory
import spring.webmvc.domain.repository.TransportRepository

@Component
class TransportStrategy(
    private val transportRepository: TransportRepository,
) : ProductAttributeStrategy {
    override fun category() = ProductCategory.TRANSPORT

    override fun findByProductId(productId: Long): ProductAttributeResult {
        val transport = transportRepository.findByProductId(productId)

        return TransportResult.of(transport)
    }

    override fun create(product: Product, command: ProductAttributePutCommand): ProductAttributeResult {
        val transportCommand = command as TransportPutCommand

        val transport = Transport.create(
            product = product,
            departureLocation = transportCommand.departureLocation,
            arrivalLocation = transportCommand.arrivalLocation,
            departureTime = transportCommand.departureTime,
            arrivalTime = transportCommand.arrivalTime,
        )

        transportRepository.save(transport)

        return TransportResult.of(transport)
    }

    override fun update(productId: Long, command: ProductAttributePutCommand): ProductAttributeResult {
        val transportCommand = command as TransportPutCommand

        val transport = transportRepository.findByProductId(productId)

        transport.update(
            departureLocation = transportCommand.departureLocation,
            arrivalLocation = transportCommand.arrivalLocation,
            departureTime = transportCommand.departureTime,
            arrivalTime = transportCommand.arrivalTime,
        )

        return TransportResult.of(transport)
    }

    override fun deleteProduct(productId: Long) {
        val transport = transportRepository.findByProductId(productId)

        transportRepository.delete(transport)
    }
}
