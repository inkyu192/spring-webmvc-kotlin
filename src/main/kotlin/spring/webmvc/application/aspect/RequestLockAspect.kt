package spring.webmvc.application.aspect

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component
import spring.webmvc.domain.repository.LockCacheRepository
import spring.webmvc.application.exception.DuplicateRequestException

@Aspect
@Component
class RequestLockAspect(
    private val httpServletRequest: HttpServletRequest,
    private val lockCacheRepository: LockCacheRepository,
    private val objectMapper: ObjectMapper,
) {

    @Pointcut("@annotation(spring.webmvc.application.aspect.RequestLock)")
    fun preventDuplicateRequestAnnotation() {
    }

    @Around("preventDuplicateRequestAnnotation()")
    fun preventDuplicateRequest(joinPoint: ProceedingJoinPoint): Any {
        val uri = httpServletRequest.requestURI
        val method = httpServletRequest.method

        val isSuccess = lockCacheRepository.tryLock(
            method = method,
            uri = uri,
            hash = objectMapper.writeValueAsString(joinPoint.args)
        )

        if (!isSuccess) {
            throw DuplicateRequestException(method = method, uri = uri)
        }

        return joinPoint.proceed()
    }
}