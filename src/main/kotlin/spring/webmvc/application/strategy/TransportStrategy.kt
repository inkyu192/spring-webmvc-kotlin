package spring.webmvc.application.strategy

import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.ProductPropertyPutCommand
import spring.webmvc.application.dto.command.TransportPutCommand
import spring.webmvc.application.dto.result.ProductPropertyResult
import spring.webmvc.application.dto.result.TransportResult
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.Transport
import spring.webmvc.domain.model.enums.ProductCategory
import spring.webmvc.domain.repository.TransportRepository

@Component
class TransportStrategy(
    private val transportRepository: TransportRepository,
) : ProductPropertyStrategy {
    override fun category() = ProductCategory.TRANSPORT

    override fun findByProductId(productId: Long): ProductPropertyResult {
        val transport = transportRepository.findByProductId(productId)

        return TransportResult.from(transport)
    }

    override fun create(product: Product, command: ProductPropertyPutCommand): ProductPropertyResult {
        val transportCommand = command as TransportPutCommand

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

    override fun replace(productId: Long, command: ProductPropertyPutCommand): ProductPropertyResult {
        val transportCommand = command as TransportPutCommand

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