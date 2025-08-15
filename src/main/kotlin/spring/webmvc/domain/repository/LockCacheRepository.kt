package spring.webmvc.domain.repository

interface LockCacheRepository {
    fun tryLock(method: String, uri: String, hash: String): Boolean
}