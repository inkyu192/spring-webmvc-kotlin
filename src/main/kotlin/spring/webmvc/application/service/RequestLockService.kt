package spring.webmvc.application.service

import org.springframework.stereotype.Service
import spring.webmvc.domain.repository.RequestLockRepository
import spring.webmvc.presentation.exception.DuplicateRequestException

@Service
class RequestLockService(
    private val requestLockRepository: RequestLockRepository
) {
    fun validate(memberId: Long, method: String, uri: String) {
        if (!requestLockRepository.setIfAbsent(memberId = memberId, method = method, uri = uri)) {
            throw DuplicateRequestException(memberId = memberId, method = method, uri = uri)
        }
    }
}