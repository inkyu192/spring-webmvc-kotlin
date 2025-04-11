package spring.webmvc.presentation.exception.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import spring.webmvc.infrastructure.util.ProblemDetailUtil
import spring.webmvc.infrastructure.util.ResponseWriter

@Component
class AccessDeniedExceptionHandler(
    private val problemDetailUtil: ProblemDetailUtil,
    private val responseWriter: ResponseWriter,
) : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        exception: AccessDeniedException?
    ) {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception?.message).apply {
            type = problemDetailUtil.createType(HttpStatus.FORBIDDEN)
        }

        responseWriter.writeResponse(problemDetail)
    }
}