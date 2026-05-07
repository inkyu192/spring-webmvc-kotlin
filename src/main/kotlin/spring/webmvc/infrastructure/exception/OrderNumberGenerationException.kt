package spring.webmvc.infrastructure.exception

import org.springframework.http.HttpStatus

class OrderNumberGenerationException : AbstractHttpException(
    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
)
