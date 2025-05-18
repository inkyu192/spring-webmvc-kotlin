package spring.webmvc.presentation.exception

import org.springframework.http.HttpStatus

class InsufficientQuantityException(
    productName: String,
    requestedQuantity: Long,
    availableStock: Long,
) : AbstractHttpException(
    message = "$productName 상품의 재고가 부족합니다. (요청: $requestedQuantity, 재고: $availableStock)",
    httpStatus = HttpStatus.CONFLICT,
)