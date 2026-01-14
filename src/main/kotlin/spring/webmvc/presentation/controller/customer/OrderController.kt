package spring.webmvc.presentation.controller.customer

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.aspect.RequestLock
import spring.webmvc.application.dto.query.OrderCursorPageQuery
import spring.webmvc.application.service.OrderService
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.infrastructure.security.SecurityContextUtil
import spring.webmvc.presentation.dto.request.OrderCreateRequest
import spring.webmvc.presentation.dto.response.CursorPageResponse
import spring.webmvc.presentation.dto.response.OrderDetailResponse
import spring.webmvc.presentation.dto.response.OrderSummaryResponse
import java.time.Instant

@RestController("customerOrderController")
@RequestMapping("/customer/orders")
class OrderController(
    private val orderService: OrderService,
) {
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @RequestLock
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrder(
        @RequestBody @Validated orderCreateRequest: OrderCreateRequest,
    ): OrderDetailResponse {
        val command = orderCreateRequest.toCommand(
            userId = SecurityContextUtil.getUserId(),
        )

        val orderResult = orderService.createOrder(command = command)

        return OrderDetailResponse.from(orderResult)
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    fun findOrders(
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(required = false) orderStatus: OrderStatus?,
        @RequestParam(required = false) orderedFrom: Instant?,
        @RequestParam(required = false) orderedTo: Instant?,
    ): CursorPageResponse<OrderSummaryResponse> {
        val query = OrderCursorPageQuery(
            cursorId = cursorId,
            userId = SecurityContextUtil.getUserId(),
            orderStatus = orderStatus,
            orderedFrom = orderedFrom,
            orderedTo = orderedTo,
        )

        val page = orderService.findOrdersWithCursorPage(query = query)

        return CursorPageResponse.from(page) { OrderSummaryResponse.from(result = it) }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun findOrder(@PathVariable id: Long): OrderDetailResponse {
        val orderResult = orderService.findOrderByUser(
            id = id,
            userId = SecurityContextUtil.getUserId(),
        )

        return OrderDetailResponse.from(orderResult)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @RequestLock
    fun cancelOrder(@PathVariable id: Long): OrderDetailResponse {
        val orderResult = orderService.cancelOrder(id)

        return OrderDetailResponse.from(orderResult)
    }
}
