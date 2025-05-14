package spring.webmvc.infrastructure.aspect

import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component
import spring.webmvc.application.service.RequestLockService
import spring.webmvc.infrastructure.security.SecurityContextUtil

@Aspect
@Component
class RequestLockAspect(
    private val httpServletRequest: HttpServletRequest,
    private val requestLockService: RequestLockService
) {

    @Pointcut("@annotation(spring.webmvc.infrastructure.aspect.RequestLock)")
    fun preventDuplicateRequestAnnotation() {
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    fun withinRestController() {
    }

    @Pointcut("within(@org.springframework.security.access.prepost.PreAuthorize *)")
    fun withinPreAuthorize() {
    }

    @Around("preventDuplicateRequestAnnotation() && withinRestController() && withinPreAuthorize()")
    fun preventDuplicateRequest(joinPoint: ProceedingJoinPoint): Any {
        val uri = httpServletRequest.requestURI
        val method = httpServletRequest.method
        val memberId = SecurityContextUtil.getMemberId()

        requestLockService.validate(memberId = memberId, method = method, uri = uri)

        return joinPoint.proceed()
    }
}