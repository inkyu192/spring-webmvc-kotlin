package spring.webmvc.presentation.exception

import org.springframework.http.HttpStatus

abstract class AbstractValidationException(message: String) :
    AbstractHttpException(message = message, httpStatus = HttpStatus.BAD_REQUEST)