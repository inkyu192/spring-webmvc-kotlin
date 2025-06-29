package spring.webmvc.application.strategy

import spring.webmvc.application.dto.command.ProductCreateCommand
import spring.webmvc.application.dto.command.ProductUpdateCommand
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.domain.model.enums.Category

interface ProductStrategy {
    fun category(): Category
    fun findByProductId(productId: Long): ProductResult
    fun createProduct(productCreateCommand: ProductCreateCommand): ProductResult
    fun updateProduct(productId: Long, productUpdateCommand: ProductUpdateCommand): ProductResult
    fun deleteProduct(productId: Long)
}