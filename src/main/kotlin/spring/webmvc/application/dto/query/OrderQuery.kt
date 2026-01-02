package spring.webmvc.application.dto.query

import org.springframework.data.domain.Pageable
import spring.webmvc.domain.model.enums.OrderStatus

data class OrderFindQuery(
    val userId: Long,
    val pageable: Pageable,
    val orderStatus: OrderStatus?,
)

data class OrderFindByIdQuery(
    val userId: Long,
    val id: Long,
)
