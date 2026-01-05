package spring.webmvc.application.strategy

import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.ProductAttributeCreateCommand
import spring.webmvc.application.dto.command.ProductAttributeUpdateCommand
import spring.webmvc.application.dto.command.TransportCreateCommand
import spring.webmvc.application.dto.command.TransportUpdateCommand
import spring.webmvc.application.dto.result.ProductAttributeResult
import spring.webmvc.application.dto.result.TransportResult
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.Transport
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.TransportRepository

@Component
class TransportStrategy(
    private val transportRepository: TransportRepository,
) : ProductAttributeStrategy {
    override fun category() = Category.TRANSPORT

    override fun findByProductId(productId: Long): ProductAttributeResult {
        val transport = transportRepository.findByProductId(productId)

        return TransportResult.from(transport)
    }

    override fun createProduct(product: Product, command: ProductAttributeCreateCommand): ProductAttributeResult {
        val transportCommand = command as TransportCreateCommand

        val transport = Transport.create(
            product = product,
            departureLocation = transportCommand.departureLocation,
            arrivalLocation = transportCommand.arrivalLocation,
            departureTime = transportCommand.departureTime,
            arrivalTime = transportCommand.arrivalTime,
        )

        transportRepository.save(transport)

        return TransportResult.from(transport)
    }

    override fun updateProduct(productId: Long, command: ProductAttributeUpdateCommand): ProductAttributeResult {
        val transportCommand = command as TransportUpdateCommand

        val transport = transportRepository.findByProductId(productId)

        transport.update(
            departureLocation = transportCommand.departureLocation,
            arrivalLocation = transportCommand.arrivalLocation,
            departureTime = transportCommand.departureTime,
            arrivalTime = transportCommand.arrivalTime,
        )

        return TransportResult.from(transport)
    }

    override fun deleteProduct(productId: Long) {
        val transport = transportRepository.findByProductId(productId)

        transportRepository.delete(transport)
    }
}