package spring.webmvc.application.strategy

import spring.webmvc.application.dto.command.ProductPutCommand
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.Category

interface ProductStrategy {
    fun category(): Category
    fun findByProductId(productId: Long): ProductResult
    fun createProduct(product: Product, command: ProductPutCommand): ProductResult
    fun updateProduct(productId: Long, command: ProductPutCommand): ProductResult
    fun deleteProduct(productId: Long)
}
