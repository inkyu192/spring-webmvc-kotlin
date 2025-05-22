package spring.webmvc.application.service

import org.springframework.stereotype.Service
import spring.webmvc.domain.cache.CacheKey
import spring.webmvc.domain.cache.ValueCache
import spring.webmvc.presentation.exception.DuplicateRequestException

@Service
class RequestLockService(
    private val valueCache: ValueCache,
) {
    fun validate(memberId: Long, method: String, uri: String) {
        val key = CacheKey.REQUEST_LOCK.generate(memberId, method, uri)

        if (!valueCache.setIfAbsent(key = key, value = "1", timeout = CacheKey.REQUEST_LOCK.timeOut)) {
            throw DuplicateRequestException(memberId = memberId, method = method, uri = uri)
        }
    }
}