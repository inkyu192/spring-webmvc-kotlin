package spring.webmvc.application.service

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.command.ProductCreateCommand
import spring.webmvc.application.dto.command.ProductUpdateCommand
import spring.webmvc.application.dto.query.ProductCursorPageQuery
import spring.webmvc.application.dto.query.ProductOffsetPageQuery
import spring.webmvc.application.dto.result.ProductDetailResult
import spring.webmvc.application.dto.result.ProductSummaryResult
import spring.webmvc.application.event.ProductViewEvent
import spring.webmvc.application.strategy.ProductAttributeStrategy
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.repository.ProductRepository

@Service
@Transactional(readOnly = true)
class ProductService(
    private val productRepository: ProductRepository,
    private val productAttributeStrategyMap: Map<Category, ProductAttributeStrategy>,
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
        val strategy = productAttributeStrategyMap[product.category]
            ?: throw UnsupportedOperationException("${product.category}")

        val attributeResult = strategy.findByProductId(productId = id)

        return ProductDetailResult.from(product = product, attributeResult = attributeResult)
    }

    fun incrementProductViewCount(id: Long) {
        ProductViewEvent(productId = id).let { eventPublisher.publishEvent(it) }
    }

    @Transactional
    fun createProduct(command: ProductCreateCommand): ProductDetailResult {
        val product = Product.create(
            category = command.category,
            name = command.name,
            description = command.description,
            price = command.price,
            quantity = command.quantity,
        )

        productRepository.save(product)

        val strategy = productAttributeStrategyMap[command.category]
            ?: throw UnsupportedOperationException("${command.category}")

        val attributeResult = strategy.createProduct(product = product, command = command.attribute)

        return ProductDetailResult.from(product = product, attributeResult = attributeResult)
    }

    @Transactional
    @Caching(
        evict = [
            CacheEvict(value = ["product"], key = "'product:' + #command.id"),
            CacheEvict(value = ["productStock"], key = "'product:' + #command.id + ':stock'"),
        ]
    )
    fun updateProduct(command: ProductUpdateCommand): ProductDetailResult {
        val product = productRepository.findById(command.id)

        product.update(
            status = command.status,
            name = command.name,
            description = command.description,
            price = command.price,
            quantity = command.quantity,
        )

        val strategy = productAttributeStrategyMap[product.category]
            ?: throw UnsupportedOperationException("${product.category}")

        val attributeResult = strategy.updateProduct(productId = command.id, command = command.attribute)

        return ProductDetailResult.from(product = product, attributeResult = attributeResult)
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
        val strategy = productAttributeStrategyMap[product.category]
            ?: throw UnsupportedOperationException("${product.category}")

        strategy.deleteProduct(productId = id)
        productRepository.delete(product)
    }
}
