package spring.webmvc.domain.repository.cache

interface RequestLockCacheRepository {
    fun tryLock(method: String, uri: String, hash: String): Boolean
}