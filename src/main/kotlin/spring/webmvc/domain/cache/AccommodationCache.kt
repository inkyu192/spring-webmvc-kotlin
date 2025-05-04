package spring.webmvc.domain.cache

interface AccommodationCache {
    fun get(id: Long): String?
    fun set(id: Long, value: String)
}