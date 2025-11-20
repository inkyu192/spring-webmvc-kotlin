package spring.webmvc.presentation.exception

import org.springframework.http.HttpStatus
import kotlin.reflect.KClass

class EntityNotFoundException(kClass: KClass<*>, id: Any) :
    AbstractHttpException(
        message = "${kClass.simpleName} 엔티티를 찾을 수 없습니다. (ID: $id)",
        httpStatus = HttpStatus.NOT_FOUND,
    )