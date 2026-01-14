package spring.webmvc.infrastructure.exception

import org.springframework.http.HttpStatus
import kotlin.reflect.KClass

class InvalidEntityStatusException(
    kClass: KClass<*>,
    id: Long,
    fromStatus: String,
    toStatus: String,
) : AbstractHttpException(
    message = "${kClass.simpleName}(ID: $id)의 상태를 ${fromStatus}에서 ${toStatus}(으)로 변경할 수 없습니다.",
    httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
)