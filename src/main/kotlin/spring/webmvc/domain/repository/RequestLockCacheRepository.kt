package spring.webmvc.domain.repository

interface RequestLockCacheRepository {
    fun tryLock(method: String, uri: String, hash: String): Boolean
}