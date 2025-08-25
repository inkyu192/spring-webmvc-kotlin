package spring.webmvc.domain.repository.cache

import spring.webmvc.domain.model.cache.TicketCache

interface TicketCacheRepository {
    fun getTicket(productId: Long): TicketCache?
    fun setTicket(productId: Long, ticketCache: TicketCache)
    fun deleteTicket(productId: Long): Boolean
}