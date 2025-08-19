package spring.webmvc.application.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.command.OrderCreateCommand
import spring.webmvc.domain.cache.CacheKey
import spring.webmvc.domain.cache.ValueCache
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.domain.repository.OrderRepository
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.infrastructure.security.SecurityContextUtil
import spring.webmvc.presentation.exception.EntityNotFoundException
import spring.webmvc.presentation.exception.InsufficientQuantityException

@Service
@Transactional(readOnly = true)
class OrderService(
    private val valueCache: ValueCache,
    private val memberRepository: MemberRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
) {
    @Transactional
    fun createOrder(orderCreateCommand: OrderCreateCommand): Order {
        val memberId = SecurityContextUtil.getMemberId()
        val member = memberRepository.findByIdOrNull(id = memberId)
            ?: throw EntityNotFoundException(kClass = Member::class, id = memberId)

        val productMap = productRepository.findByIds(ids = orderCreateCommand.products.map { it.id })
            .associateBy { it.id }

        val order = Order.create(member = member)

        orderCreateCommand.products.forEach {
            val product = productMap[it.id]
                ?: throw EntityNotFoundException(kClass = Product::class, id = it.id)

            val key = CacheKey.PRODUCT_STOCK.generate(it.id)
            val stock = valueCache.decrement(key = key, delta = it.quantity)

            if (stock == null || stock < 0) {
                if (stock != null) {
                    valueCache.increment(key = key, delta = it.quantity)
                }
                throw InsufficientQuantityException(
                    productName = product.name,
                    requestedQuantity = it.quantity,
                    availableStock = valueCache.get(key = key, clazz = Long::class.java) ?: 0L
                )
            }

            order.addProduct(product = product, quantity = it.quantity)
        }

        return runCatching { orderRepository.save(order) }
            .getOrElse { e ->
                orderCreateCommand.products.forEach {
                    val key = CacheKey.PRODUCT_STOCK.generate(it.id)
                    valueCache.increment(key = key, delta = it.quantity)
                }
                throw e
            }
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