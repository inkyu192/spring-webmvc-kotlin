package spring.webmvc.presentation.exception.handler

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import spring.webmvc.infrastructure.properties.AppProperties
import java.net.URI
import java.nio.charset.StandardCharsets

@Component
class AuthenticationExceptionHandler(
    private val appProperties: AppProperties,
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException,
    ) {
        val status = HttpStatus.UNAUTHORIZED
        val problemDetail = ProblemDetail.forStatusAndDetail(status, exception.message)
            .apply { type = URI.create("${appProperties.docsUrl}#${status}") }

        response.status = status.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.writer.write(objectMapper.writeValueAsString(problemDetail))
    }
}
