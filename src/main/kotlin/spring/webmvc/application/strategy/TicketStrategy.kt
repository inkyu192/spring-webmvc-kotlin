package spring.webmvc.application.strategy

import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.ProductCreateCommand
import spring.webmvc.application.dto.command.ProductUpdateCommand
import spring.webmvc.application.dto.command.TicketCreateCommand
import spring.webmvc.application.dto.command.TicketUpdateCommand
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.application.dto.result.TicketResult
import spring.webmvc.domain.cache.CacheKey
import spring.webmvc.domain.cache.ValueCache
import spring.webmvc.domain.model.entity.Ticket
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.TicketRepository
import spring.webmvc.presentation.exception.EntityNotFoundException

@Component
class TicketStrategy(
    private val valueCache: ValueCache,
    private val ticketRepository: TicketRepository,
) : ProductStrategy {
    override fun category() = Category.TICKET

    override fun findByProductId(productId: Long): ProductResult {
        val key = CacheKey.TICKET.generate(productId)
        val cache = valueCache.get(key = key, clazz = TicketResult::class.java)

        if (cache != null) {
            return cache
        }

        val ticketResult = ticketRepository.findByProductId(productId)
            ?.let { TicketResult(ticket = it) }
            ?: throw EntityNotFoundException(kClass = Ticket::class, id = productId)

        valueCache.set(key = key, value = ticketResult, timeout = CacheKey.TICKET.timeOut)

        return ticketResult
    }

    override fun createProduct(productCreateCommand: ProductCreateCommand): ProductResult {
        val ticketCreateCommand = productCreateCommand as TicketCreateCommand

        val ticket = ticketRepository.save(
            Ticket.create(
                name = ticketCreateCommand.name,
                description = ticketCreateCommand.description,
                price = ticketCreateCommand.price,
                quantity = ticketCreateCommand.quantity,
                place = ticketCreateCommand.place,
                performanceTime = ticketCreateCommand.performanceTime,
                duration = ticketCreateCommand.duration,
                ageLimit = ticketCreateCommand.ageLimit,
            )
        )

        return TicketResult(ticket)
    }

    override fun updateProduct(productId: Long, productUpdateCommand: ProductUpdateCommand): ProductResult {
        val ticketUpdateCommand = productUpdateCommand as TicketUpdateCommand

        val ticket = ticketRepository.findByProductId(productId)
            ?: throw EntityNotFoundException(kClass = Ticket::class, id = productId)

        ticket.update(
            name = ticketUpdateCommand.name,
            description = ticketUpdateCommand.description,
            price = ticketUpdateCommand.price,
            quantity = ticketUpdateCommand.quantity,
            place = ticketUpdateCommand.place,
            performanceTime = ticketUpdateCommand.performanceTime,
            duration = ticketUpdateCommand.duration,
            ageLimit = ticketUpdateCommand.ageLimit,
        )

        return TicketResult(ticket)
    }

    override fun deleteProduct(productId: Long) {
        val ticket = ticketRepository.findByProductId(productId)
            ?: throw EntityNotFoundException(kClass = Ticket::class, id = productId)

        ticketRepository.delete(ticket)

        val key = CacheKey.TICKET.generate(productId)
        valueCache.delete(key = key)
    }
}