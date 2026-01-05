package spring.webmvc.infrastructure.exception

import org.springframework.http.HttpStatus
import kotlin.reflect.KClass

class NotFoundEntityException(kClass: KClass<*>, id: Any) :
    AbstractHttpException(
        message = "${kClass.simpleName} 엔티티를 찾을 수 없습니다. (ID: $id)",
        httpStatus = HttpStatus.NOT_FOUND,
    )