package spring.webmvc.infrastructure.exception

import org.springframework.http.HttpStatus

class InsufficientQuantityException(
    productName: String,
    requestQuantity: Long,
    stock: Long,
) : AbstractHttpException(
    message = "$productName 상품의 재고가 부족합니다. (요청수량: $requestQuantity, 재고: $stock)",
    httpStatus = HttpStatus.CONFLICT,
)