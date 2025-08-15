package spring.webmvc.presentation.controller

import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.OrderService
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.application.aspect.RequestLock
import spring.webmvc.presentation.dto.request.OrderCreateRequest
import spring.webmvc.presentation.dto.response.OrderResponse

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService
) {
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @RequestLock
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrder(@RequestBody @Validated orderCreateRequest: OrderCreateRequest): OrderResponse {
        val command = orderCreateRequest.toCommand()
        return OrderResponse(order = orderService.createOrder(orderCreateCommand = command))
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    fun findOrders(
        @PageableDefault pageable: Pageable,
        @RequestParam(required = false) orderStatus: OrderStatus?,
    ) = orderService.findOrders(pageable = pageable, orderStatus = orderStatus).map { OrderResponse(order = it) }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun findOrder(@PathVariable id: Long) = OrderResponse(order = orderService.findOrder(id = id))

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @RequestLock
    fun cancelOrder(@PathVariable id: Long) = OrderResponse(order = orderService.cancelOrder(id = id))
}