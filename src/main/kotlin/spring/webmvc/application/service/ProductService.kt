package spring.webmvc.application.service

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.command.ProductCreateCommand
import spring.webmvc.application.dto.command.ProductUpdateCommand
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.application.strategy.ProductStrategy
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.presentation.exception.EntityNotFoundException
import spring.webmvc.presentation.exception.StrategyNotImplementedException

@Service
@Transactional(readOnly = true)
class ProductService(
    private val productRepository: ProductRepository,
    private val productStrategies: List<ProductStrategy>,
) {
    fun findProducts(pageable: Pageable, name: String?) =
        productRepository.findAll(pageable = pageable, name = name).map { ProductResult(product = it) }

    fun findProduct(category: Category, id: Long): ProductResult {
        val productStrategy = getProductStrategy(category)

        return productStrategy.findByProductId(productId = id)
    }

    @Transactional
    fun createProduct(command: ProductCreateCommand): ProductResult {
        val productStrategy = getProductStrategy(category = command.category)

        return productStrategy.createProduct(productCreateCommand = command)
    }

    @Transactional
    fun updateProduct(id: Long, productUpdateCommand: ProductUpdateCommand): ProductResult {
        productRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(kClass = Product::class, id = id)

        val productStrategy = getProductStrategy(category = productUpdateCommand.category)

        return productStrategy.updateProduct(productId = id, productUpdateCommand = productUpdateCommand)
    }

    @Transactional
    fun deleteProduct(category: Category, id: Long) {
        productRepository.findByIdOrNull(id)
            ?: throw EntityNotFoundException(kClass = Product::class, id = id)

        val productStrategy = getProductStrategy(category = category)

        productStrategy.deleteProduct(productId = id)
    }

    private fun getProductStrategy(category: Category) =
        productStrategies.firstOrNull { it.supports(category) }
            ?: throw StrategyNotImplementedException(kClass = ProductStrategy::class, category = category)
}