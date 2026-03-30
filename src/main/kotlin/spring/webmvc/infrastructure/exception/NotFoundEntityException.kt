package spring.webmvc.infrastructure.exception

import org.springframework.http.HttpStatus
import kotlin.reflect.KClass

class NotFoundEntityException(kClass: KClass<*>, id: Any) :
    AbstractHttpException(
        httpStatus = HttpStatus.NOT_FOUND,
        messageArgs = arrayOf(kClass.java.simpleName, id.toString()),
    )
