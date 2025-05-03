package spring.webmvc.application.service

import org.springframework.stereotype.Service
import spring.webmvc.domain.cache.RequestLockCache
import spring.webmvc.presentation.exception.DuplicateRequestException

@Service
class RequestLockService(
    private val requestLockCache: RequestLockCache,
) {
    fun validate(memberId: Long, method: String, uri: String) {
        if (!requestLockCache.setIfAbsent(memberId = memberId, method = method, uri = uri)) {
            throw DuplicateRequestException(memberId = memberId, method = method, uri = uri)
        }
    }
}