package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.command.ProductCreateCommand
import spring.webmvc.application.dto.command.ProductUpdateCommand
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.application.strategy.ProductStrategy
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.domain.repository.cache.ProductCacheRepository
import spring.webmvc.infrastructure.exception.StrategyNotImplementedException

@Service
@Transactional(readOnly = true)
class ProductService(
    private val productCacheRepository: ProductCacheRepository,
    private val productRepository: ProductRepository,
    private val productStrategyMap: Map<Category, ProductStrategy>,
) {
    fun findProducts(cursorId: Long?, size: Int, name: String?) =
        productRepository.findWithCursorPage(cursorId = cursorId, size = size, name = name)

    fun findProduct(category: Category, id: Long): ProductResult {
        val productStrategy = productStrategyMap[category]
            ?: throw StrategyNotImplementedException(kClass = ProductStrategy::class, category = category)
        val productResult = productStrategy.findByProductId(productId = id)

        productCacheRepository.incrementProductViewCount(productId = id, delta = 1)

        return productResult
    }

    @Transactional
    fun createProduct(command: ProductCreateCommand): ProductResult {
        val productStrategy = productStrategyMap[command.category]
            ?: throw StrategyNotImplementedException(kClass = ProductStrategy::class, category = command.category)
        val productResult = productStrategy.createProduct(productCreateCommand = command)

        productCacheRepository.deleteProductStock(productId = productResult.id)

        return productResult
    }

    @Transactional
    fun updateProduct(id: Long, productUpdateCommand: ProductUpdateCommand): ProductResult {
        val productStrategy = productStrategyMap[productUpdateCommand.category]
            ?: throw StrategyNotImplementedException(
                kClass = ProductStrategy::class,
                category = productUpdateCommand.category
            )
        val productResult = productStrategy.updateProduct(productId = id, productUpdateCommand = productUpdateCommand)

        productCacheRepository.deleteProductStock(productId = id)

        return productResult
    }

    @Transactional
    fun deleteProduct(category: Category, id: Long) {
        val productStrategy = productStrategyMap[category]
            ?: throw StrategyNotImplementedException(kClass = ProductStrategy::class, category = category)
        productStrategy.deleteProduct(productId = id)

        productCacheRepository.deleteProductStock(productId = id)
    }
}