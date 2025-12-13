package spring.webmvc.infrastructure.exception

import org.springframework.http.HttpStatus

class DuplicateRequestException(method: String, uri: String) :
    AbstractHttpException(
        message = "요청이 중복되었습니다. ($method $uri)",
        httpStatus = HttpStatus.TOO_MANY_REQUESTS,
    )