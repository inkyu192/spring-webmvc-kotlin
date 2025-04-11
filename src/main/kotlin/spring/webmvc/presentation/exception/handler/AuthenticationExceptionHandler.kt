package spring.webmvc.presentation.exception.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import spring.webmvc.infrastructure.util.ProblemDetailUtil
import spring.webmvc.infrastructure.util.ResponseWriter

@Component
class AuthenticationExceptionHandler(
    private val problemDetailUtil: ProblemDetailUtil,
    private val responseWriter: ResponseWriter,
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        exception: AuthenticationException?
    ) {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception?.message).apply {
            type = problemDetailUtil.createType(HttpStatus.UNAUTHORIZED)
        }

        responseWriter.writeResponse(problemDetail)
    }
}