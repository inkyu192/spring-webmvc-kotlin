package spring.webmvc.application.service

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.command.ProductDeleteCommand
import spring.webmvc.application.dto.command.ProductPutCommand
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.application.event.ProductViewEvent
import spring.webmvc.application.strategy.ProductStrategy
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.ProductRepository

@Service
@Transactional(readOnly = true)
class ProductService(
    private val productRepository: ProductRepository,
    private val productStrategyMap: Map<Category, ProductStrategy>,
    private val eventPublisher: ApplicationEventPublisher,
) {
    fun findProducts(cursorId: Long?, size: Int, name: String?) =
        productRepository.findWithCursorPage(cursorId = cursorId, size = size, name = name)

    @Cacheable(value = ["product"], key = "'product:' + #id")
    fun findProduct(category: Category, id: Long): ProductResult {
        eventPublisher.publishEvent(ProductViewEvent(productId = id))

        val strategy = productStrategyMap[category] ?: throw UnsupportedOperationException("$category")

        return strategy.findByProductId(id)
    }

    @Transactional
    fun createProduct(command: ProductPutCommand): ProductResult {
        val product = productRepository.save(
            Product.create(
                category = command.category,
                name = command.name,
                description = command.description,
                price = command.price,
                quantity = command.quantity,
            )
        )

        val strategy = productStrategyMap[command.category]
            ?: throw UnsupportedOperationException("${command.category}")

        return strategy.createProduct(product, command)
    }

    @Transactional
    @Caching(
        evict = [
            CacheEvict(value = ["product"], key = "'product:' + #command.id"),
            CacheEvict(value = ["productStock"], key = "'product:' + #command.id + ':stock'"),
        ]
    )
    fun updateProduct(command: ProductPutCommand): ProductResult {
        val product = productRepository.findById(checkNotNull(command.id))

        product.update(
            name = command.name,
            description = command.description,
            price = command.price,
            quantity = command.quantity,
        )

        val strategy = productStrategyMap[command.category]
            ?: throw UnsupportedOperationException("${command.category}")

        return strategy.updateProduct(checkNotNull(command.id), command)
    }

    @Transactional
    @Caching(
        evict = [
            CacheEvict(value = ["product"], key = "'product:' + #command.id"),
            CacheEvict(value = ["productStock"], key = "'product:' + #command.id + ':stock'"),
        ]
    )
    fun deleteProduct(command: ProductDeleteCommand) {
        val product = productRepository.findById(command.id)
        productRepository.delete(product)

        val strategy = productStrategyMap[command.category]
            ?: throw UnsupportedOperationException("${command.category}")

        strategy.deleteProduct(command.id)
    }
}
