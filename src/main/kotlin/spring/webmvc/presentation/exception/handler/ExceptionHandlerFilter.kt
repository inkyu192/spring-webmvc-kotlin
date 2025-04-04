package spring.webmvc.presentation.exception.handler

import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import spring.webmvc.infrastructure.util.ResponseWriter

@Component
class ExceptionHandlerFilter(
    private val responseWriter: ResponseWriter
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        runCatching { filterChain.doFilter(request, response) }
            .onFailure { throwable ->
                when (throwable) {
                    is JwtException -> handleException(HttpStatus.UNAUTHORIZED, throwable.message)

                    is Exception -> handleException(HttpStatus.INTERNAL_SERVER_ERROR, throwable.message)
                }
            }
    }

    private fun handleException(status: HttpStatus, message: String?) {
        responseWriter.writeResponse(ProblemDetail.forStatusAndDetail(status, message))
    }
}
