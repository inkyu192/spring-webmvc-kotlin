package spring.webmvc.application.strategy

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.ProductCreateCommand
import spring.webmvc.application.dto.command.TicketCreateCommand
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.application.dto.result.TicketResult
import spring.webmvc.domain.cache.TicketCache
import spring.webmvc.domain.model.entity.Ticket
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.TicketRepository
import spring.webmvc.presentation.exception.EntityNotFoundException

@Component
class TicketStrategy(
    private val ticketCache: TicketCache,
    private val ticketRepository: TicketRepository,
    private val objectMapper: ObjectMapper,
) : ProductStrategy {
    private val logger = LoggerFactory.getLogger(TicketStrategy::class.java)

    override fun supports(category: Category) = category == Category.TICKET

    override fun findByProductId(productId: Long): ProductResult {
        val cache = ticketCache.get(productId)
            ?.let { value ->
                runCatching { objectMapper.readValue(value, TicketResult::class.java) }
                    .onFailure {
                        logger.warn("Failed to deserialize cache for productId={}: {}", productId, it.message)
                    }
                    .getOrNull()
            }

        if (cache != null) {
            return cache
        }

        val ticketResult = ticketRepository.findByProductId(productId)
            ?.let { TicketResult(ticket = it) }
            ?: throw EntityNotFoundException(kClass = Ticket::class, id = productId)

        runCatching { objectMapper.writeValueAsString(ticketResult) }
            .onSuccess { value -> ticketCache.set(id = productId, value = value) }
            .onFailure { logger.warn("Failed to serialize cache for productId={}: {}", productId, it.message) }

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
}