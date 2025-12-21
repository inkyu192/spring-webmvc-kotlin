package spring.webmvc.infrastructure.exception

import org.springframework.http.HttpStatus

abstract class AbstractHttpException(
    val httpStatus: HttpStatus,
    message: String,
    throwable: Throwable? = null,
) : RuntimeException(message, throwable)