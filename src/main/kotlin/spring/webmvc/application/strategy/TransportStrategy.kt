package spring.webmvc.application.strategy

import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.ProductCreateCommand
import spring.webmvc.application.dto.command.ProductUpdateCommand
import spring.webmvc.application.dto.command.TransportCreateCommand
import spring.webmvc.application.dto.command.TransportUpdateCommand
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.application.dto.result.TransportResult
import spring.webmvc.domain.model.cache.TransportCache
import spring.webmvc.domain.model.entity.Transport
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.TransportRepository
import spring.webmvc.domain.repository.cache.TransportCacheRepository

@Component
class TransportStrategy(
    private val transportCacheRepository: TransportCacheRepository,
    private val transportRepository: TransportRepository,
) : ProductStrategy {
    override fun category() = Category.TRANSPORT

    override fun findByProductId(productId: Long): ProductResult {
        val cached = transportCacheRepository.getTransport(productId)

        if (cached != null) {
            return TransportResult(transportCache = cached)
        }

        val transport = transportRepository.findById(productId)

        transportCacheRepository.setTransport(
            productId = productId,
            transportCache = TransportCache.create(
                id = productId,
                name = transport.product.name,
                description = transport.product.description,
                price = transport.product.price,
                quantity = transport.product.quantity,
                createdAt = transport.product.createdAt,
                transportId = checkNotNull(transport.id),
                departureLocation = transport.departureLocation,
                arrivalLocation = transport.arrivalLocation,
                departureTime = transport.departureTime,
                arrivalTime = transport.arrivalTime
            )
        )

        return TransportResult(transport)
    }

    override fun createProduct(productCreateCommand: ProductCreateCommand): ProductResult {
        val transportCreateCommand = productCreateCommand as TransportCreateCommand

        val transport = transportRepository.save(
            Transport.create(
                name = transportCreateCommand.name,
                description = transportCreateCommand.description,
                price = transportCreateCommand.price,
                quantity = transportCreateCommand.quantity,
                departureLocation = transportCreateCommand.departureLocation,
                arrivalLocation = transportCreateCommand.arrivalLocation,
                departureTime = transportCreateCommand.departureTime,
                arrivalTime = transportCreateCommand.arrivalTime,
            )
        )

        return TransportResult(transport)
    }

    override fun updateProduct(productId: Long, productUpdateCommand: ProductUpdateCommand): ProductResult {
        val transportUpdateCommand = productUpdateCommand as TransportUpdateCommand

        val transport = transportRepository.findById(productId)

        transport.update(
            name = transportUpdateCommand.name,
            description = transportUpdateCommand.description,
            price = transportUpdateCommand.price,
            quantity = transportUpdateCommand.quantity,
            departureLocation = transportUpdateCommand.departureLocation,
            arrivalLocation = transportUpdateCommand.arrivalLocation,
            departureTime = transportUpdateCommand.departureTime,
            arrivalTime = transportUpdateCommand.arrivalTime,
        )

        return TransportResult(transport)
    }

    override fun deleteProduct(productId: Long) {
        val transport = transportRepository.findById(productId)

        transportRepository.delete(transport)

        transportCacheRepository.deleteTransport(productId)
    }
}
