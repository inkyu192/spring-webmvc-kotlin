package spring.webmvc.presentation.exception.handler

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.ErrorResponse
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException
import spring.webmvc.infrastructure.exception.AbstractHttpException
import spring.webmvc.infrastructure.properties.AppProperties
import java.net.URI

@RestControllerAdvice
class ApplicationExceptionHandler(
    private val appProperties: AppProperties,
) {
    @ExceptionHandler(AbstractHttpException::class)
    fun handleBusinessException(e: AbstractHttpException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(e.httpStatus, e.message).apply {
            type = createDocsUri(status)
        }

    @ExceptionHandler(
        NoResourceFoundException::class,
        HttpRequestMethodNotSupportedException::class,
        ServletRequestBindingException::class,
    )
    fun handleResourceNotFound(errorResponse: ErrorResponse): ProblemDetail =
        errorResponse.body.apply { type = createDocsUri(status) }

    @ExceptionHandler(
        HttpMessageNotReadableException::class,
        MethodArgumentTypeMismatchException::class,
    )
    fun handleInvalidRequestBody(exception: Exception): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.message).apply {
            type = createDocsUri(HttpStatus.BAD_REQUEST)
        }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(exception: MethodArgumentNotValidException): ProblemDetail =
        exception.body.apply {
            type = createDocsUri(status)
            setProperty("fields", exception.bindingResult.fieldErrors.associate { it.field to it.defaultMessage })
        }

    private fun createDocsUri(status: HttpStatus): URI =
        URI.create("${appProperties.docsUrl}#${status.name}")

    private fun createDocsUri(statusCode: Int): URI {
        val status = HttpStatus.resolve(statusCode) ?: return URI.create("about:blank")
        return createDocsUri(status)
    }
}