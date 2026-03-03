package spring.webmvc.infrastructure.exception

import org.springframework.http.HttpStatus
import kotlin.reflect.KClass

class NotFoundEntityException(kClass: KClass<*>, id: Any) :
    AbstractHttpException(
        httpStatus = HttpStatus.NOT_FOUND,
        translationArgs = arrayOf(kClass.java.simpleName, id),
    )
