package spring.webmvc.presentation.controller.customer

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.aspect.RequestLock
import spring.webmvc.application.dto.command.OrderCancelCommand
import spring.webmvc.application.dto.query.OrderFindByIdQuery
import spring.webmvc.application.dto.query.OrderFindQuery
import spring.webmvc.application.service.OrderService
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.infrastructure.security.SecurityContextUtil
import spring.webmvc.presentation.dto.request.OrderCreateRequest
import spring.webmvc.presentation.dto.response.OrderResponse

@RestController
@RequestMapping("/customer/orders")
class CustomerOrderController(
    private val orderService: OrderService,
) {
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @RequestLock
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrder(@RequestBody @Validated orderCreateRequest: OrderCreateRequest): OrderResponse {
        val userId = SecurityContextUtil.getUserId()
        val command = orderCreateRequest.toCommand(userId = userId)
        return OrderResponse.from(orderService.createOrder(command = command))
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    fun findOrders(
        @PageableDefault pageable: Pageable,
        @RequestParam(required = false) orderStatus: OrderStatus?,
    ): Page<OrderResponse> {
        val userId = SecurityContextUtil.getUserId()
        val query = OrderFindQuery(userId = userId, pageable = pageable, orderStatus = orderStatus)
        return orderService.findOrders(query = query).map { OrderResponse.from(it) }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun findOrder(@PathVariable id: Long): OrderResponse {
        val userId = SecurityContextUtil.getUserId()
        val query = OrderFindByIdQuery(userId = userId, id = id)
        return OrderResponse.from(orderService.findOrder(query = query))
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @RequestLock
    fun cancelOrder(@PathVariable id: Long): OrderResponse {
        val userId = SecurityContextUtil.getUserId()
        val command = OrderCancelCommand(userId = userId, id = id)
        return OrderResponse.from(orderService.cancelOrder(command = command))
    }
}
