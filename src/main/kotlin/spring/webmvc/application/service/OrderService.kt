package spring.webmvc.application.service

import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.command.OrderCancelCommand
import spring.webmvc.application.dto.command.OrderCreateCommand
import spring.webmvc.application.dto.command.OrderProductCreateCommand
import spring.webmvc.application.dto.query.OrderFindByIdQuery
import spring.webmvc.application.dto.query.OrderFindQuery
import spring.webmvc.application.dto.result.OrderResult
import spring.webmvc.domain.model.entity.Order
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.repository.OrderRepository
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.domain.repository.UserRepository
import spring.webmvc.domain.repository.cache.ProductCacheRepository
import spring.webmvc.infrastructure.exception.EntityNotFoundException
import spring.webmvc.infrastructure.exception.InsufficientQuantityException

@Service
@Transactional(readOnly = true)
class OrderService(
    private val productCacheRepository: ProductCacheRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
) {
    @Transactional
    fun createOrder(command: OrderCreateCommand): OrderResult {
        val user = userRepository.findById(id = command.userId)

        val productMap = productRepository.findAllById(ids = command.products.map { it.id })
            .associateBy { it.id }

        val order = Order.create(user = user)
        val processedProducts = mutableListOf<OrderProductCreateCommand>()

        try {
            command.products.forEach { orderProductCreateCommand ->
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

            val savedOrder = orderRepository.save(order)
            return OrderResult.from(savedOrder)
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

    fun findOrders(query: OrderFindQuery): Page<OrderResult> {
        val user = userRepository.findById(id = query.userId)

        return orderRepository.findAll(
            pageable = query.pageable,
            user = user,
            orderStatus = query.orderStatus
        ).map { OrderResult.from(it) }
    }

    fun findOrder(query: OrderFindByIdQuery): OrderResult {
        val user = userRepository.findById(id = query.userId)

        val order = orderRepository.findByIdAndUser(id = query.id, user = user)
            ?: throw EntityNotFoundException(kClass = Order::class, id = query.id)

        return OrderResult.from(order)
    }

    @Transactional
    fun cancelOrder(command: OrderCancelCommand): OrderResult {
        val user = userRepository.findById(id = command.userId)

        val order = orderRepository.findByIdAndUser(id = command.id, user = user)
            ?: throw EntityNotFoundException(kClass = Order::class, id = command.id)

        order.cancel()

        return OrderResult.from(order)
    }
}