package spring.webmvc.domain.cache

interface FlightCache {
    fun get(id: Long): String?
    fun set(id: Long, value: String)
}