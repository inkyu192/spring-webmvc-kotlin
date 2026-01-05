package spring.webmvc.infrastructure.exception

import org.springframework.http.HttpStatus
import spring.webmvc.domain.model.enums.OrderStatus

class InvalidOrderStatusException(
    orderId: Long,
    currentStatus: OrderStatus,
    targetStatus: OrderStatus,
) : AbstractHttpException(
    message = "주문(ID: $orderId)의 상태를 ${currentStatus.description}에서 ${targetStatus.description}(으)로 변경할 수 없습니다.",
    httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
)