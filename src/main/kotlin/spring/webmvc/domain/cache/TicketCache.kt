package spring.webmvc.domain.cache

interface TicketCache {
    fun get(id: Long): String?
    fun set(id: Long, value: String)
}