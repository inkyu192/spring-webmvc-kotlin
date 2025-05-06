package spring.webmvc.application.strategy

import org.springframework.stereotype.Component
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.application.dto.result.TicketResult
import spring.webmvc.domain.cache.TicketCache
import spring.webmvc.domain.model.entity.Ticket
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.TicketRepository
import spring.webmvc.infrastructure.common.JsonSupport
import spring.webmvc.presentation.exception.EntityNotFoundException

@Component
class TicketStrategy(
    private val ticketCache: TicketCache,
    private val ticketRepository: TicketRepository,
    private val jsonSupport: JsonSupport,
) : ProductStrategy {
    override fun supports(category: Category) = category == Category.TICKET

    override fun findByProductId(productId: Long): ProductResult {
        val cached = ticketCache.get(productId)
            ?.let { jsonSupport.readValue(json = it, clazz = TicketResult::class.java) }

        if (cached != null) {
            return cached
        }

        val ticketResult = ticketRepository.findByProductId(productId)
            ?.let { TicketResult(ticket = it) }
            ?: throw EntityNotFoundException(kClass = Ticket::class, id = productId)

        jsonSupport.writeValueAsString(obj = ticketResult)
            ?.let { ticketCache.set(id = productId, value = it) }

        return ticketResult
    }
}