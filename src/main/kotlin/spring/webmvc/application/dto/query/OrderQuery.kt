package spring.webmvc.application.dto.query

import org.springframework.data.domain.Pageable
import spring.webmvc.domain.model.enums.OrderStatus
import java.time.Instant

data class OrderOffsetPageQuery(
    val pageable: Pageable,
    val userId: Long?,
    val orderStatus: OrderStatus?,
    val orderedFrom: Instant?,
    val orderedTo: Instant?,
)

data class OrderCursorPageQuery(
    val cursorId: Long?,
    val userId: Long?,
    val orderStatus: OrderStatus?,
    val orderedFrom: Instant?,
    val orderedTo: Instant?,
)