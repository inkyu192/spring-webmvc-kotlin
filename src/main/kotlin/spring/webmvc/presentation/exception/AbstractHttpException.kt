package spring.webmvc.presentation.exception

import org.springframework.http.HttpStatus

abstract class AbstractHttpException(
    val httpStatus: HttpStatus,
    message: String,
    throwable: Throwable? = null,
): RuntimeException(message, throwable)