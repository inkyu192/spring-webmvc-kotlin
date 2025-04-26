package spring.webmvc.application.service

import org.springframework.data.domain.Page
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
import spring.webmvc.infrastructure.util.SecurityContextUtil
import spring.webmvc.presentation.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class OrderService(
    private val memberRepository: MemberRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository
) {
    @Transactional
    fun createOrder(productQuantities: List<Pair<Long, Int>>): Order {
        val memberId = SecurityContextUtil.getMemberId()

        val member = memberRepository.findByIdOrNull(id = memberId)
            ?: throw EntityNotFoundException(clazz = Member::class.java, id = memberId)

        val order = Order.create(member = member)

        val productMap = productRepository.findAllById(ids = productQuantities.map { it.first }).associateBy { it.id }
        productQuantities.forEach {
            val product = productMap[it.first]
                ?: throw EntityNotFoundException(clazz = OrderItem::class.java, id = it.first)

            order.addProduct(product = product, quantity = it.second)
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
            ?: throw EntityNotFoundException(clazz = Order::class.java, id = id)

        return order
    }

    @Transactional
    fun cancelOrder(id: Long): Order {
        val memberId = SecurityContextUtil.getMemberId()

        val order = orderRepository.findByIdAndMemberId(id = id, memberId = memberId)
            ?: throw EntityNotFoundException(clazz = Order::class.java, id = id)

        order.cancel()

        return order
    }
}