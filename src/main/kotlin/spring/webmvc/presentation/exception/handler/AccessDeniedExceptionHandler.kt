package spring.webmvc.presentation.exception.handler

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import spring.webmvc.application.service.TranslationService
import spring.webmvc.infrastructure.properties.AppProperties
import java.net.URI
import java.nio.charset.StandardCharsets

@Component
class AccessDeniedExceptionHandler(
    private val appProperties: AppProperties,
    private val objectMapper: ObjectMapper,
    private val translationService: TranslationService,
) : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AccessDeniedException,
    ) {
        val status = HttpStatus.FORBIDDEN
        val locale = LocaleContextHolder.getLocale()
        val detail = translationService.getMessageOrNull(
            code = exception::class.java.simpleName,
            locale = locale,
        ) ?: translationService.getMessage(AccessDeniedException::class.java.simpleName, locale)
        val problemDetail = ProblemDetail.forStatusAndDetail(status, detail)
            .apply { type = URI.create("${appProperties.docsUrl}#$status") }

        response.status = status.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.writer.write(objectMapper.writeValueAsString(problemDetail))
    }
}
