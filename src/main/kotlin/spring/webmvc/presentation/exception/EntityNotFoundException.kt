package spring.webmvc.presentation.exception

import org.springframework.http.HttpStatus

class EntityNotFoundException(clazz: Class<*>, id: Long) :
    AbstractHttpException(
        message = "${clazz.simpleName} 엔티티를 찾을 수 없습니다. (ID: $id)",
        httpStatus = HttpStatus.NOT_FOUND,
    )