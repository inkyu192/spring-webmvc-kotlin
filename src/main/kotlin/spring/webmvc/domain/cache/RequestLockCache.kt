package spring.webmvc.domain.cache

interface RequestLockCache {
    fun setIfAbsent(memberId: Long, method: String, uri: String): Boolean
}