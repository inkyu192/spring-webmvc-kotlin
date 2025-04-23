package spring.webmvc.presentation.exception

import org.springframework.http.HttpStatus

abstract class AbstractHttpException(
    message: String,
    val httpStatus: HttpStatus,
    throwable: Throwable? = null,
): RuntimeException(message, throwable)