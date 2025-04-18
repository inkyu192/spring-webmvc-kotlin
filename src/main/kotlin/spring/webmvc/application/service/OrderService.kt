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
        val (memberId, city, street, zipcode, orderItems) = orderSaveRequest

        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw EntityNotFoundException(clazz = Member::class.java, id = memberId)

        val order = Order.create(member)

        val itemMap = itemRepository.findAllById(orderItems.map { it.itemId }).associateBy { it.id }
        orderItems.forEach {
            val item = itemMap[it.itemId]
                ?: throw EntityNotFoundException(clazz = OrderItem::class.java, id = it.itemId)

            order.addItem(item = item, count = it.count)
        }

        orderRepository.save(order)

        return OrderResponse(order)
    }

    fun findOrders(
        pageable: Pageable,
        memberId: Long?,
        orderStatus: OrderStatus?,
    ): Page<OrderResponse> {
        return orderRepository.findAll(pageable = pageable, memberId = memberId, orderStatus = orderStatus)
            .map(::OrderResponse)
    }

    fun findOrder(id: Long): OrderResponse {
        val order = orderRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(clazz = Order::class.java, id = id)

        return OrderResponse(order)
    }

    @Transactional
    fun cancelOrder(id: Long): OrderResponse {
        val order = orderRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(clazz = Order::class.java, id = id)

        order.cancel()

        return OrderResponse(order)
    }
}