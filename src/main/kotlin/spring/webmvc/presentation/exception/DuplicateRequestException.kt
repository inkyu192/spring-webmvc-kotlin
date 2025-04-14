package spring.webmvc.presentation.exception

import org.springframework.http.HttpStatus

class DuplicateRequestException(memberId: Long, method: String, uri: String) :
    AbstractHttpException(
        message = "회원(ID: $memberId)의 요청이 중복되었습니다. ($method $uri)",
        httpStatus = HttpStatus.TOO_MANY_REQUESTS,
    )