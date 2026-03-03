package spring.webmvc.presentation.exception.handler

import org.springframework.context.i18n.LocaleContextHolder
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
import spring.webmvc.domain.repository.cache.TranslationCacheRepository
import spring.webmvc.infrastructure.exception.AbstractHttpException
import spring.webmvc.infrastructure.properties.AppProperties
import java.net.URI

@RestControllerAdvice
class ApplicationExceptionHandler(
    private val appProperties: AppProperties,
    private val translationCacheRepository: TranslationCacheRepository,
) {
    @ExceptionHandler(AbstractHttpException::class)
    fun handleBusinessException(e: AbstractHttpException): ProblemDetail {
        val locale = LocaleContextHolder.getLocale()
        val detail = translationCacheRepository.getMessage(e.translationCode, locale, e.translationArgs)

        return ProblemDetail.forStatusAndDetail(e.httpStatus, detail).apply {
            type = URI.create("${appProperties.docsUrl}#$status")
        }
    }

    @ExceptionHandler(
        HttpMessageNotReadableException::class,
        MethodArgumentTypeMismatchException::class,
        MethodArgumentConversionNotSupportedException::class,
    )
    fun handleInvalidRequestBody(exception: Exception): ProblemDetail {
        val locale = LocaleContextHolder.getLocale()

        val translationCode = exception::class.java.simpleName
        val detail = translationCacheRepository.getMessage(translationCode, locale)

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail).apply {
            type = URI.create("${appProperties.docsUrl}#$status")
        }
    }

    @ExceptionHandler(
        NoResourceFoundException::class,
        HttpRequestMethodNotSupportedException::class,
        ServletRequestBindingException::class,
    )
    fun handleResourceNotFound(errorResponse: ErrorResponse): ProblemDetail {
        val locale = LocaleContextHolder.getLocale()
        val translationCode = errorResponse::class.java.simpleName
        val detail = translationCacheRepository.getMessage(translationCode, locale)

        return ProblemDetail.forStatusAndDetail(errorResponse.statusCode, detail).apply {
            type = URI.create("${appProperties.docsUrl}#$status")
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(exception: MethodArgumentNotValidException): ProblemDetail {
        val locale = LocaleContextHolder.getLocale()
        val translationCode = exception::class.java.simpleName
        val detail = translationCacheRepository.getMessage(translationCode, locale)

        return ProblemDetail.forStatusAndDetail(exception.statusCode, detail).apply {
            type = URI.create("${appProperties.docsUrl}#$status")
            setProperty("fields", exception.bindingResult.fieldErrors.associate { fieldError ->
                val fieldTranslationCode = "$translationCode.${fieldError.code}"
                val args = fieldError.arguments?.drop(1)?.toTypedArray() ?: emptyArray()
                val resolvedMessage = translationCacheRepository.getMessageOrNull(fieldTranslationCode, locale, args)
                    ?: translationCacheRepository.getMessage("$translationCode.default", locale)
                fieldError.field to resolvedMessage
            })
        }
    }
}
