package spring.webmvc.presentation.exception

import org.springframework.http.HttpStatus

class DuplicateEntityException(clazz: Class<*>, name: String) :
    AbstractHttpException(
        message = "이미 존재하는 ${clazz.simpleName} 엔티티입니다. (ID: '$name')",
        httpStatus = HttpStatus.CONFLICT,
    )