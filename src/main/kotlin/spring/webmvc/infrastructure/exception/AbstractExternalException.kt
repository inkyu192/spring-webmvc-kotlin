package spring.webmvc.infrastructure.exception

abstract class AbstractExternalException(
    val messageArgs: Array<Any> = emptyArray(),
    throwable: Throwable,
) : RuntimeException(throwable) {
    open val translationCode: String
        get() = this::class.java.simpleName
}
