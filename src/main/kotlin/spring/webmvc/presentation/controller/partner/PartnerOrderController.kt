package spring.webmvc.presentation.controller.partner

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.dto.query.OrderFindByIdQuery
import spring.webmvc.application.dto.query.OrderFindQuery
import spring.webmvc.application.service.OrderService
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.presentation.dto.response.OrderResponse

@RestController
@RequestMapping("/partner/orders")
class PartnerOrderController(
    private val orderService: OrderService,
) {
    @GetMapping
    @PreAuthorize("hasAuthority('ORDER_READ')")
    fun findOrders(
        @RequestParam userId: Long,
        @PageableDefault pageable: Pageable,
        @RequestParam(required = false) orderStatus: OrderStatus?,
    ): Page<OrderResponse> {
        val query = OrderFindQuery(userId = userId, pageable = pageable, orderStatus = orderStatus)
        return orderService.findOrders(query = query).map { OrderResponse.from(it) }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ORDER_READ')")
    fun findOrder(
        @RequestParam userId: Long,
        @PathVariable id: Long
    ): OrderResponse {
        val query = OrderFindByIdQuery(userId = userId, id = id)
        return OrderResponse.from(orderService.findOrder(query = query))
    }
}