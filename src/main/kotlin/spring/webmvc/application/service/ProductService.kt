package spring.webmvc.application.service

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.command.ProductPutCommand
import spring.webmvc.application.dto.query.ProductCursorPageQuery
import spring.webmvc.application.dto.query.ProductOffsetPageQuery
import spring.webmvc.application.dto.result.ProductDetailResult
import spring.webmvc.application.dto.result.ProductSummaryResult
import spring.webmvc.application.event.ProductViewEvent
import spring.webmvc.application.strategy.ProductPropertyStrategy
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.ProductCategory
import spring.webmvc.domain.repository.ProductRepository

@Service
@Transactional(readOnly = true)
class ProductService(
    private val productRepository: ProductRepository,
    private val productPropertyStrategyMap: Map<ProductCategory, ProductPropertyStrategy>,
    private val eventPublisher: ApplicationEventPublisher,
) {
    fun findProductsWithCursorPage(query: ProductCursorPageQuery) =
        productRepository.findAllWithCursorPage(query = query)
            .map { ProductSummaryResult.from(product = it) }

    fun findProductsWithOffsetPage(query: ProductOffsetPageQuery): Page<ProductSummaryResult> =
        productRepository.findAllWithOffsetPage(query = query)
            .map { ProductSummaryResult.from(product = it) }

    @Cacheable(value = ["product"], key = "'product:' + #id")
    fun findProductCached(id: Long): ProductDetailResult {
        return findProduct(id)
    }

    fun findProduct(id: Long): ProductDetailResult {
        val product = productRepository.findById(id)
        val strategy = productPropertyStrategyMap[product.category]
            ?: throw UnsupportedOperationException("${product.category}")

        val propertyResult = strategy.findByProductId(productId = id)

        return ProductDetailResult.from(product = product, propertyResult = propertyResult)
    }

    fun incrementProductViewCount(id: Long) {
        ProductViewEvent(productId = id).let { eventPublisher.publishEvent(it) }
    }

    @Transactional
    fun createProduct(command: ProductPutCommand): ProductDetailResult {
        val product = Product.create(
            category = command.category,
            name = command.name,
            description = command.description,
            price = command.price,
            quantity = command.quantity,
            exposureProperty = command.exposureProperty,
        )

        productRepository.save(product)

        val strategy = productPropertyStrategyMap[command.category]
            ?: throw UnsupportedOperationException("${command.category}")

        val propertyResult = strategy.create(product = product, command = command.property)

        return ProductDetailResult.from(product = product, propertyResult = propertyResult)
    }

    @Transactional
    @Caching(
        evict = [
            CacheEvict(value = ["product"], key = "'product:' + #command.id"),
            CacheEvict(value = ["productStock"], key = "'product:' + #command.id + ':stock'"),
        ]
    )
    fun replaceProduct(command: ProductPutCommand): ProductDetailResult {
        val id = requireNotNull(command.id)

        val product = productRepository.findById(id)

        product.replace(
            status = command.status,
            name = command.name,
            description = command.description,
            price = command.price,
            quantity = command.quantity,
            exposureProperty = command.exposureProperty,
        )

        val strategy = productPropertyStrategyMap[product.category]
            ?: throw UnsupportedOperationException("${product.category}")

        val propertyResult = strategy.replace(productId = command.id, command = command.property)

        return ProductDetailResult.from(product = product, propertyResult = propertyResult)
    }

    @Transactional
    @Caching(
        evict = [
            CacheEvict(value = ["product"], key = "'product:' + #id"),
            CacheEvict(value = ["productStock"], key = "'product:' + #id + ':stock'"),
        ]
    )
    fun deleteProduct(id: Long) {
        val product = productRepository.findById(id)
        val strategy = productPropertyStrategyMap[product.category]
            ?: throw UnsupportedOperationException("${product.category}")

        strategy.deleteProduct(productId = id)
        productRepository.delete(product)
    }
}
