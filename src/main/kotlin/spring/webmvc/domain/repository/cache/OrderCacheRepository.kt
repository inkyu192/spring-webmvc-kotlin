package spring.webmvc.domain.repository.cache

interface OrderCacheRepository {
    fun incrementSequence(date: String): Long?
    fun setSequence(date: String, value: Long): Boolean
}
