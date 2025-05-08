package spring.webmvc.application.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.command.OrderCreateCommand
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.entity.OrderItem
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.domain.repository.OrderRepository
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.infrastructure.util.SecurityContextUtil
import spring.webmvc.presentation.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class OrderService(
    private val memberRepository: MemberRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
) {
    @Transactional
    fun createOrder(orderCreateCommand: OrderCreateCommand): Order {
        val memberId = SecurityContextUtil.getMemberId()

        val member = memberRepository.findByIdOrNull(id = memberId)
            ?: throw EntityNotFoundException(kClass = Member::class, id = memberId)

        val order = Order.create(member = member)

        val productMap = productRepository.findAllById(
            ids = orderCreateCommand.products.map { it.productId }
        ).associateBy { it.id }

        orderCreateCommand.products.forEach {
            val product = productMap[it.productId]
                ?: throw EntityNotFoundException(kClass = OrderItem::class, id = it.productId)

            order.addProduct(product = product, quantity = it.quantity)
        }

        return orderRepository.save(order = order)
    }

    fun findOrders(pageable: Pageable, orderStatus: OrderStatus?): Page<Order> {
        val memberId = SecurityContextUtil.getMemberId()

        return orderRepository.findAll(
            pageable = pageable,
            memberId = memberId,
            orderStatus = orderStatus
        )
    }

    fun findOrder(id: Long): Order {
        val memberId = SecurityContextUtil.getMemberId()

        val order = orderRepository.findByIdAndMemberId(id = id, memberId = memberId)
            ?: throw EntityNotFoundException(kClass = Order::class, id = id)

        return order
    }

    @Transactional
    fun cancelOrder(id: Long): Order {
        val memberId = SecurityContextUtil.getMemberId()

        val order = orderRepository.findByIdAndMemberId(id = id, memberId = memberId)
            ?: throw EntityNotFoundException(kClass = Order::class, id = id)

        order.cancel()

        return order
    }
}