package spring.webmvc.domain.repository.cache

import spring.webmvc.application.dto.result.TicketResult
import java.time.Duration

interface TicketCacheRepository {
    fun getTicket(productId: Long): TicketResult?
    fun setTicket(productId: Long, ticketResult: TicketResult, timeout: Duration? = null)
    fun deleteTicket(productId: Long): Boolean
}