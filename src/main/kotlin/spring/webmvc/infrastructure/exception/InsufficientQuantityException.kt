package spring.webmvc.infrastructure.exception

import org.springframework.http.HttpStatus

class InsufficientQuantityException(
    productName: String,
    requestQuantity: Long,
    stock: Long,
) : AbstractHttpException(
    httpStatus = HttpStatus.CONFLICT,
    messageArgs = arrayOf(productName, requestQuantity, stock),
)
