package spring.webmvc.infrastructure.exception

import org.springframework.http.HttpStatus
import kotlin.reflect.KClass

class DuplicateEntityException(kClass: KClass<*>, name: String) :
    AbstractHttpException(
        httpStatus = HttpStatus.CONFLICT,
        translationArgs = arrayOf(kClass.java.simpleName, name),
    )
