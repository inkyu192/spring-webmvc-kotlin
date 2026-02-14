package spring.webmvc.application.service

import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.command.OrderCreateCommand
import spring.webmvc.application.dto.command.OrderProductCreateCommand
import spring.webmvc.application.dto.command.OrderStatusUpdateCommand
import spring.webmvc.application.dto.query.OrderCursorPageQuery
import spring.webmvc.application.dto.query.OrderOffsetPageQuery
import spring.webmvc.application.dto.result.OrderDetailResult
import spring.webmvc.application.dto.result.OrderSummaryResult
import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.repository.OrderRepository
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.domain.repository.UserRepository
import spring.webmvc.domain.repository.cache.ProductCacheRepository
import spring.webmvc.infrastructure.exception.InsufficientQuantityException
import spring.webmvc.infrastructure.exception.NotFoundEntityException
import spring.webmvc.infrastructure.security.SecurityContextUtil
import java.time.Duration

@Service
@Transactional(readOnly = true)
class OrderService(
    private val productCacheRepository: ProductCacheRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
) {
    @Transactional
    fun createOrder(command: OrderCreateCommand): OrderDetailResult {
        val user = userRepository.findById(id = command.userId)
        val productMap = productRepository.findAllById(ids = command.products.map { it.id }).associateBy { it.id }

        val order = Order.create(user = user)
        val processedProducts = mutableListOf<OrderProductCreateCommand>()

        try {
            command.products.forEach { orderProductCreateCommand ->
                val product = productMap[orderProductCreateCommand.id]
                    ?: throw NotFoundEntityException(kClass = Product::class, id = orderProductCreateCommand.id)

                // 캐시 없을경우 초기화
                if (productCacheRepository.getProductStock(productId = orderProductCreateCommand.id) == null) {
                    productCacheRepository.setProductStockIfAbsent(
                        productId = orderProductCreateCommand.id,
                        stock = product.quantity,
                        timeout = Duration.ofHours(1)
                    )
                }

                // 원자적 재고 감소 처리
                val stock = productCacheRepository.decrementProductStock(
                    productId = orderProductCreateCommand.id,
                    delta = orderProductCreateCommand.quantity,
                )

                if (stock == null || stock < 0) {
                    if (stock != null) {
                        productCacheRepository.incrementProductStock(
                            productId = orderProductCreateCommand.id,
                            delta = orderProductCreateCommand.quantity,
                        )
                    }

                    throw InsufficientQuantityException(
                        productName = product.name,
                        requestQuantity = orderProductCreateCommand.quantity,
                        stock = productCacheRepository.getProductStock(productId = orderProductCreateCommand.id) ?: 0L
                    )
                }

                // 성공시 주문 처리
                order.addProduct(product = product, quantity = orderProductCreateCommand.quantity)
                    .also { processedProducts.add(orderProductCreateCommand) }
            }

            orderRepository.save(order)

            return OrderDetailResult.of(order)
        } catch (e: Exception) {
            processedProducts.forEach { orderProductCreateCommand ->
                productCacheRepository.incrementProductStock(
                    productId = orderProductCreateCommand.id,
                    delta = orderProductCreateCommand.quantity,
                )
            }

            throw e
        }
    }

    fun findOrdersWithOffsetPage(query: OrderOffsetPageQuery): Page<OrderSummaryResult> =
        orderRepository.findAllWithOffsetPage(
            pageable = query.pageable,
            userId = query.userId,
            orderStatus = query.orderStatus,
            orderedFrom = query.orderedFrom,
            orderedTo = query.orderedTo,
        ).map { OrderSummaryResult.of(order = it) }

    fun findOrdersWithCursorPage(query: OrderCursorPageQuery) =
        orderRepository.findAllWithCursorPage(
            cursorId = query.cursorId,
            userId = query.userId,
            orderStatus = query.orderStatus,
            orderedFrom = query.orderedFrom,
            orderedTo = query.orderedTo,
        ).map { OrderSummaryResult.of(order = it) }

    fun findOrder(id: Long): OrderDetailResult {
        val order = orderRepository.findById(id = id)

        return OrderDetailResult.of(order)
    }

    fun findOrderByUser(id: Long, userId: Long): OrderDetailResult {
        val order = orderRepository.findByIdAndUserId(id = id, userId = userId)
            ?: throw NotFoundEntityException(kClass = Order::class, id = id)

        return OrderDetailResult.of(order)
    }

    @Transactional
    fun updateOrderStatus(command: OrderStatusUpdateCommand): OrderDetailResult {
        val order = orderRepository.findById(id = command.id)

        order.updateStatus(status = command.orderStatus)

        return OrderDetailResult.of(order)
    }

    @Transactional
    fun cancelOrder(id: Long): OrderDetailResult {
        val userId = SecurityContextUtil.getUserId()
        val order = orderRepository.findByIdAndUserId(id = id, userId = userId)
            ?: throw NotFoundEntityException(kClass = Order::class, id = id)

        order.cancel()

        return OrderDetailResult.of(order)
    }
}
