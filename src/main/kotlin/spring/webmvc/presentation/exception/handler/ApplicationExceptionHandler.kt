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
import spring.webmvc.infrastructure.util.ProblemDetailUtil
import spring.webmvc.presentation.exception.AbstractHttpException
import spring.webmvc.presentation.exception.AtLeastOneRequiredException

@RestControllerAdvice
class ApplicationExceptionHandler(
    private val problemDetailUtil: ProblemDetailUtil,
) {

    @ExceptionHandler(AbstractHttpException::class)
    fun handleBusinessException(e: AbstractHttpException) =
        ProblemDetail.forStatusAndDetail(e.httpStatus, e.message).apply {
            type = problemDetailUtil.createType(status) ?: type

            if (e is AtLeastOneRequiredException) {
                setProperty("fields", e.fields)
            }
        }

    @ExceptionHandler(
        NoResourceFoundException::class,
        HttpRequestMethodNotSupportedException::class,
        ServletRequestBindingException::class,
    )
    fun handleResourceNotFound(errorResponse: ErrorResponse) =
        errorResponse.body.apply { type = problemDetailUtil.createType(status) ?: type }

    @ExceptionHandler(HttpMessageNotReadableException::class, MethodArgumentTypeMismatchException::class)
    fun handleInvalidRequestBody(exception: Exception) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.message).apply {
            type = problemDetailUtil.createType(HttpStatus.BAD_REQUEST)
        }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(exception: MethodArgumentNotValidException) =
        exception.body.apply {
            type = problemDetailUtil.createType(status) ?: type
            setProperty("fields", exception.bindingResult.fieldErrors.associate { it.field to it.defaultMessage })
        }
}