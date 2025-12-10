package spring.webmvc.application.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.command.OrderCreateCommand
import spring.webmvc.application.dto.command.OrderProductCreateCommand
import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.OrderStatus
import spring.webmvc.domain.repository.OrderRepository
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.domain.repository.UserRepository
import spring.webmvc.domain.repository.cache.ProductCacheRepository
import spring.webmvc.infrastructure.security.SecurityContextUtil
import spring.webmvc.presentation.exception.EntityNotFoundException
import spring.webmvc.presentation.exception.InsufficientQuantityException

@Service
@Transactional(readOnly = true)
class OrderService(
    private val productCacheRepository: ProductCacheRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
) {
    @Transactional
    fun createOrder(orderCreateCommand: OrderCreateCommand): Order {
        val userId = SecurityContextUtil.getUserId()
        val user = userRepository.findById(id = userId)

        val productMap = productRepository.findAllById(ids = orderCreateCommand.products.map { it.id })
            .associateBy { it.id }

        val order = Order.create(user = user)
        val processedProducts = mutableListOf<OrderProductCreateCommand>()

        try {
            orderCreateCommand.products.forEach { orderProductCreateCommand ->
                val product = productMap[orderProductCreateCommand.id]
                    ?: throw EntityNotFoundException(kClass = Product::class, id = orderProductCreateCommand.id)

                // 캐시 없을경우 초기화
                if (productCacheRepository.getProductStock(productId = orderProductCreateCommand.id) == null) {
                    productCacheRepository.setProductStockIfAbsent(
                        productId = orderProductCreateCommand.id,
                        stock = product.quantity,
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
                        requestedQuantity = orderProductCreateCommand.quantity,
                        availableStock = productCacheRepository.getProductStock(productId = orderProductCreateCommand.id)
                            ?: 0L
                    )
                }

                order.addProduct(product = product, quantity = orderProductCreateCommand.quantity)
                    .also { processedProducts.add(orderProductCreateCommand) }
            }

            return orderRepository.save(order)
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

    fun findOrders(pageable: Pageable, orderStatus: OrderStatus?): Page<Order> {
        val userId = SecurityContextUtil.getUserId()

        return orderRepository.findAll(
            pageable = pageable,
            userId = userId,
            orderStatus = orderStatus
        )
    }

    fun findOrder(id: Long): Order {
        val userId = SecurityContextUtil.getUserId()

        val order = orderRepository.findByIdAndUserId(id = id, userId = userId)
            ?: throw EntityNotFoundException(kClass = Order::class, id = id)

        return order
    }

    @Transactional
    fun cancelOrder(id: Long): Order {
        val userId = SecurityContextUtil.getUserId()

        val order = orderRepository.findByIdAndUserId(id = id, userId = userId)
            ?: throw EntityNotFoundException(kClass = Order::class, id = id)

        order.cancel()

        return order
    }
}