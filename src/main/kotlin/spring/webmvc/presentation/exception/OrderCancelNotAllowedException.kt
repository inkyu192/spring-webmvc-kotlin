package spring.webmvc.presentation.exception

import org.springframework.http.HttpStatus

class OrderCancelNotAllowedException(orderId: Long) :
    AbstractHttpException("주문(ID: $orderId)은 현재 상태에서는 취소할 수 없습니다.", HttpStatus.UNPROCESSABLE_ENTITY)