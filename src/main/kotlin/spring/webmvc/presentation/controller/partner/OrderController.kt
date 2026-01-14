package spring.webmvc.presentation.controller.partner

import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.aspect.RequestLock
import spring.webmvc.application.dto.query.OrderOffsetPageQuery
import spring.webmvc.application.service.OrderService
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.presentation.dto.request.OrderStatusUpdateRequest
import spring.webmvc.presentation.dto.response.OffsetPageResponse
import spring.webmvc.presentation.dto.response.OrderDetailResponse
import spring.webmvc.presentation.dto.response.OrderSummaryResponse
import java.time.Instant

@RestController("partnerOrderController")
@RequestMapping("/partner/orders")
class OrderController(
    private val orderService: OrderService,
) {
    @GetMapping
    @PreAuthorize("hasAuthority('ORDER_READ')")
    fun findOrders(
        @PageableDefault pageable: Pageable,
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) orderStatus: OrderStatus?,
        @RequestParam(required = false) orderedFrom: Instant?,
        @RequestParam(required = false) orderedTo: Instant?,
    ): OffsetPageResponse<OrderSummaryResponse> {
        val query = OrderOffsetPageQuery(
            pageable = pageable,
            userId = userId,
            orderStatus = orderStatus,
            orderedFrom = orderedFrom,
            orderedTo = orderedTo,
        )

        val page = orderService.findOrdersWithOffsetPage(query = query)

        return OffsetPageResponse.from(page) { OrderSummaryResponse.from(result = it) }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ORDER_READ')")
    fun findOrder(
        @PathVariable id: Long,
    ): OrderDetailResponse {
        val orderResult = orderService.findOrder(id = id)

        return OrderDetailResponse.from(orderResult)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ORDER_WRITE')")
    @RequestLock
    fun updateOrderStatus(
        @PathVariable id: Long,
        @RequestBody request: OrderStatusUpdateRequest,
    ): OrderDetailResponse {
        val command = request.toCommand(id)

        val orderResult = orderService.updateOrderStatus(command = command)

        return OrderDetailResponse.from(orderResult)
    }
}