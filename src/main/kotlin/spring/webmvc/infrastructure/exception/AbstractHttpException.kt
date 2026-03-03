package spring.webmvc.infrastructure.exception

import org.springframework.http.HttpStatus

abstract class AbstractHttpException(
    val httpStatus: HttpStatus,
    val translationArgs: Array<Any> = emptyArray(),
    throwable: Throwable? = null,
) : RuntimeException(throwable) {
    open val translationCode: String
        get() = this::class.java.simpleName
}
