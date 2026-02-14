package spring.webmvc.presentation.exception.handler

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import spring.webmvc.infrastructure.properties.AppProperties
import java.net.URI
import java.nio.charset.StandardCharsets

@Component
class JwtExceptionHandler(
    private val appProperties: AppProperties,
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            val status = if (e is JwtException) {
                HttpStatus.UNAUTHORIZED
            } else {
                HttpStatus.INTERNAL_SERVER_ERROR
            }

            val problemDetail = ProblemDetail.forStatusAndDetail(status, e.message)
                .apply { type = URI.create("${appProperties.docsUrl}#${status.name}") }

            response.status = status.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = StandardCharsets.UTF_8.name()
            response.writer.write(objectMapper.writeValueAsString(problemDetail))
        }
    }
}
