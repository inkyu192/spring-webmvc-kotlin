package spring.webmvc.domain.repository

interface RequestLockRepository {
    fun setIfAbsent(memberId: Long, method: String, uri: String): Boolean
}