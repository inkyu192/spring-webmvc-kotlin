package spring.webmvc.infrastructure.exception

import org.springframework.http.HttpStatus

class DuplicateRequestException(method: String, uri: String) :
    AbstractHttpException(
        httpStatus = HttpStatus.TOO_MANY_REQUESTS,
        translationArgs = arrayOf(method, uri),
    )
