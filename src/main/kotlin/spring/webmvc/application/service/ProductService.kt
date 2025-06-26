package spring.webmvc.application.service

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.command.ProductCreateCommand
import spring.webmvc.application.dto.command.ProductUpdateCommand
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.application.strategy.ProductStrategy
import spring.webmvc.domain.cache.CacheKey
import spring.webmvc.domain.cache.ValueCache
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.presentation.exception.StrategyNotImplementedException

@Service
@Transactional(readOnly = true)
class ProductService(
    private val valueCache: ValueCache,
    private val productRepository: ProductRepository,
    private val productStrategyMap: Map<Category, ProductStrategy>,
) {
    fun findProducts(pageable: Pageable, name: String?) =
        productRepository.findAll(pageable = pageable, name = name).map { ProductResult(product = it) }

    fun findProduct(category: Category, id: Long): ProductResult {
        val productStrategy = productStrategyMap[category]
            ?: throw StrategyNotImplementedException(kClass = ProductStrategy::class, category = category)
        val productResult = productStrategy.findByProductId(productId = id)

        val key = CacheKey.PRODUCT_VIEW_COUNT.generate(id)
        valueCache.increment(key, 1)

        return productResult
    }

    @Transactional
    fun createProduct(command: ProductCreateCommand): ProductResult {
        val productStrategy = productStrategyMap[command.category]
            ?: throw StrategyNotImplementedException(kClass = ProductStrategy::class, category = command.category)
        val productResult = productStrategy.createProduct(productCreateCommand = command)

        val key = CacheKey.PRODUCT_STOCK.generate(productResult.id)
        valueCache.set(key = key, value = productResult.quantity)

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

        val key = CacheKey.PRODUCT_STOCK.generate(id)
        valueCache.set(key = key, value = productResult.quantity)

        return productResult
    }

    @Transactional
    fun deleteProduct(category: Category, id: Long) {
        val productStrategy = productStrategyMap[category]
            ?: throw StrategyNotImplementedException(kClass = ProductStrategy::class, category = category)
        productStrategy.deleteProduct(productId = id)

        val key = CacheKey.PRODUCT_STOCK.generate(id)
        valueCache.delete(key = key)
    }
}