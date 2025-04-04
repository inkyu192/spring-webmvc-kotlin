package spring.webmvc.application.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.entity.OrderItem
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.domain.repository.ItemRepository
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.domain.repository.OrderRepository
import spring.webmvc.presentation.dto.request.OrderSaveRequest
import spring.webmvc.presentation.dto.response.OrderResponse
import spring.webmvc.presentation.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class OrderService(
    private val memberRepository: MemberRepository,
    private val itemRepository: ItemRepository,
    private val orderRepository: OrderRepository
) {
    @Transactional
    fun saveOrder(orderSaveRequest: OrderSaveRequest): OrderResponse {
        val (memberId, city, street, zipcode, requestOrderItems) = orderSaveRequest

        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw EntityNotFoundException(Member::class.java, memberId)

        val orderItems = requestOrderItems.map { orderItem ->
            val item = itemRepository.findByIdOrNull(orderItem.itemId)
                ?: throw EntityNotFoundException(OrderItem::class.java, orderItem.itemId)

            OrderItem.create(item, orderItem.count)
        }

        val order = orderRepository.save(
            Order.create(
                member = member,
                orderItems = orderItems,
            )
        )

        return OrderResponse(order)
    }

    fun findOrders(
        memberId: Long?,
        orderStatus: OrderStatus?,
        pageable: Pageable
    ): Page<OrderResponse> {
        return orderRepository.findAll(pageable, memberId, orderStatus)
            .map(::OrderResponse)
    }

    fun findOrder(id: Long): OrderResponse {
        val order = orderRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(Order::class.java, id)

        return OrderResponse(order)
    }

    @Transactional
    fun cancelOrder(id: Long): OrderResponse {
        val order = orderRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(Order::class.java, id)

        order.cancel()

        return OrderResponse(order)
    }
}