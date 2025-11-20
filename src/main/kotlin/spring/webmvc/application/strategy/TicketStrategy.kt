package spring.webmvc.application.strategy

import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.ProductCreateCommand
import spring.webmvc.application.dto.command.ProductUpdateCommand
import spring.webmvc.application.dto.command.TicketCreateCommand
import spring.webmvc.application.dto.command.TicketUpdateCommand
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.application.dto.result.TicketResult
import spring.webmvc.domain.model.cache.TicketCache
import spring.webmvc.domain.model.entity.Ticket
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.TicketRepository
import spring.webmvc.domain.repository.cache.TicketCacheRepository

@Component
class TicketStrategy(
    private val ticketCacheRepository: TicketCacheRepository,
    private val ticketRepository: TicketRepository,
) : ProductStrategy {
    override fun category() = Category.TICKET

    override fun findByProductId(productId: Long): ProductResult {
        val cached = ticketCacheRepository.getTicket(productId)

        if (cached != null) {
            return TicketResult(ticketCache = cached)
        }

        val ticket = ticketRepository.findById(productId)

        ticketCacheRepository.setTicket(
            productId = productId,
            ticketCache = TicketCache.create(
                id = productId,
                name = ticket.name,
                description = ticket.description,
                price = ticket.price,
                quantity = ticket.quantity,
                createdAt = ticket.createdAt,
                ticketId = checkNotNull(ticket.id),
                place = ticket.place,
                performanceTime = ticket.performanceTime,
                duration = ticket.duration,
                ageLimit = ticket.ageLimit
            )
        )

        return TicketResult(ticket)
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

        val ticket = ticketRepository.findById(productId)

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
        val ticket = ticketRepository.findById(productId)

        ticketRepository.delete(ticket)

        ticketCacheRepository.deleteTicket(productId)
    }
}