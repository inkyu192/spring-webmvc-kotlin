package spring.webmvc.presentation.exception

import org.springframework.http.HttpStatus
import kotlin.reflect.KClass

class DuplicateEntityException(kClass: KClass<*>, name: String) :
    AbstractHttpException(
        message = "이미 존재하는 ${kClass.simpleName} 엔티티입니다. (ID: '$name')",
        httpStatus = HttpStatus.CONFLICT,
    )