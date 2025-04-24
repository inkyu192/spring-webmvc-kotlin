package spring.webmvc.presentation.exception

import org.springframework.http.HttpStatus

abstract class AbstractValidationException(
    message: String,
    vararg fields: String,
) : AbstractHttpException(message = message, httpStatus = HttpStatus.BAD_REQUEST) {
    val fields: List<String> = fields.toList()
}