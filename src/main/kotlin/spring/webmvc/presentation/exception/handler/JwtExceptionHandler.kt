package spring.webmvc.presentation.exception.handler

import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import spring.webmvc.infrastructure.util.ProblemDetailUtil
import spring.webmvc.infrastructure.util.ResponseWriter

@Component
class JwtExceptionHandler(
    private val problemDetailUtil: ProblemDetailUtil,
    private val responseWriter: ResponseWriter,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        runCatching { filterChain.doFilter(request, response) }
            .onFailure { throwable ->
                when (throwable) {
                    is JwtException -> handleException(
                        status = HttpStatus.UNAUTHORIZED,
                        message = throwable.message,
                    )

                    is Exception -> handleException(
                        status = HttpStatus.INTERNAL_SERVER_ERROR,
                        message = throwable.message,
                    )
                }
            }
    }

    private fun handleException(status: HttpStatus, message: String?) {
        val problemDetail = ProblemDetail.forStatusAndDetail(status, message).apply {
            type = problemDetailUtil.createType(status)
        }

        responseWriter.writeResponse(problemDetail)
    }
}
