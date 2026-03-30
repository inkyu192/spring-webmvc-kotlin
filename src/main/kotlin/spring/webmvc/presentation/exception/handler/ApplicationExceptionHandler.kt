package spring.webmvc.presentation.exception.handler

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.ErrorResponse
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException
import spring.webmvc.application.service.TranslationService
import spring.webmvc.infrastructure.exception.AbstractExternalException
import spring.webmvc.infrastructure.exception.AbstractHttpException
import spring.webmvc.infrastructure.properties.AppProperties
import java.net.URI
import java.util.*

@RestControllerAdvice
class ApplicationExceptionHandler(
    private val appProperties: AppProperties,
    private val translationService: TranslationService,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(AbstractHttpException::class)
    fun handleBusinessException(e: AbstractHttpException, locale: Locale): ProblemDetail {
        val detail = translationService.getMessage(e.translationCode, locale, e.messageArgs)

        return ProblemDetail.forStatusAndDetail(e.httpStatus, detail).apply {
            type = URI.create("${appProperties.docsUrl}#${HttpStatus.valueOf(status).name}")
        }
    }

    @ExceptionHandler(AbstractExternalException::class)
    fun handleExternalException(e: AbstractExternalException, locale: Locale): ProblemDetail {
        logger.error("External service error: ${e.translationCode}", e)

        val detail = translationService.getMessage(e.translationCode, locale, e.messageArgs)

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, detail).apply {
            type = URI.create("${appProperties.docsUrl}#${HttpStatus.valueOf(status).name}")
        }
    }

    @ExceptionHandler(
        HttpMessageNotReadableException::class,
        MethodArgumentTypeMismatchException::class,
        MethodArgumentConversionNotSupportedException::class,
    )
    fun handleInvalidRequestBody(exception: Exception, locale: Locale): ProblemDetail {

        val translationCode = exception::class.java.simpleName
        val detail = translationService.getMessage(translationCode, locale)

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail).apply {
            type = URI.create("${appProperties.docsUrl}#${HttpStatus.valueOf(status).name}")
        }
    }

    @ExceptionHandler(
        NoResourceFoundException::class,
        HttpRequestMethodNotSupportedException::class,
        ServletRequestBindingException::class,
    )
    fun handleResourceNotFound(errorResponse: ErrorResponse, locale: Locale): ProblemDetail {
        val translationCode = errorResponse::class.java.simpleName
        val detail = translationService.getMessage(translationCode, locale)

        return ProblemDetail.forStatusAndDetail(errorResponse.statusCode, detail).apply {
            type = URI.create("${appProperties.docsUrl}#${HttpStatus.valueOf(status).name}")
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(exception: MethodArgumentNotValidException, locale: Locale): ProblemDetail {
        val translationCode = exception::class.java.simpleName
        val detail = translationService.getMessage(translationCode, locale)

        return ProblemDetail.forStatusAndDetail(exception.statusCode, detail).apply {
            type = URI.create("${appProperties.docsUrl}#${HttpStatus.valueOf(status).name}")
            setProperty("fields", exception.bindingResult.fieldErrors.associate { fieldError ->
                val fieldTranslationCode = "$translationCode.${fieldError.code}"
                val args = fieldError.arguments?.drop(1)?.toTypedArray() ?: emptyArray()
                val resolvedMessage = translationService.getMessageOrNull(fieldTranslationCode, locale, args)
                    ?: translationService.getMessage("$translationCode.default", locale)
                fieldError.field to resolvedMessage
            })
        }
    }
}
