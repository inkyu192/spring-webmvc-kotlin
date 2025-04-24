package spring.webmvc.application.service

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.entity.OrderItem
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.domain.repository.OrderRepository
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.presentation.dto.request.OrderCreateRequest
import spring.webmvc.presentation.dto.response.OrderResponse
import spring.webmvc.presentation.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class OrderService(
    private val memberRepository: MemberRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository
) {
    @Transactional
    fun saveOrder(orderCreateRequest: OrderCreateRequest): OrderResponse {
        val (memberId, city, street, zipcode, orderProducts) = orderCreateRequest

        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw EntityNotFoundException(clazz = Member::class.java, id = memberId)

        val order = Order.create(member)

        val productMap = productRepository.findAllById(orderProducts.map { it.productId }).associateBy { it.id }
        orderProducts.forEach {
            val product = productMap[it.productId]
                ?: throw EntityNotFoundException(clazz = OrderItem::class.java, id = it.productId)

            order.addProduct(product = product, count = it.count)
        }

        orderRepository.save(order)

        return OrderResponse(order)
    }

    fun findOrders(
        pageable: Pageable,
        memberId: Long?,
        orderStatus: OrderStatus?,
    ) = orderRepository.findAll(pageable = pageable, memberId = memberId, orderStatus = orderStatus)
        .map { OrderResponse(it) }

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