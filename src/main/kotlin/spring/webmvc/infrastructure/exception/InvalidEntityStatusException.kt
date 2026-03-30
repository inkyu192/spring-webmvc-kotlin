package spring.webmvc.infrastructure.exception

import org.springframework.http.HttpStatus
import kotlin.reflect.KClass

class InvalidEntityStatusException(
    kClass: KClass<*>,
    id: Long,
    fromStatus: String,
    toStatus: String,
) : AbstractHttpException(
    httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
    messageArgs = arrayOf(kClass.java.simpleName, id, fromStatus, toStatus),
)
