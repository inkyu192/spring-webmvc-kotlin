package spring.webmvc.domain.cache

interface TokenCache {
    fun get(memberId: Long): String?
    fun set(memberId: Long, value: String)
}