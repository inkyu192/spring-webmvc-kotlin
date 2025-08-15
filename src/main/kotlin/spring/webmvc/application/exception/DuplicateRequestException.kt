package spring.webmvc.application.exception

import org.springframework.http.HttpStatus
import spring.webmvc.presentation.exception.AbstractHttpException

class DuplicateRequestException(method: String, uri: String) :
    AbstractHttpException(
        message = "요청이 중복되었습니다. ($method $uri)",
        httpStatus = HttpStatus.TOO_MANY_REQUESTS,
    )