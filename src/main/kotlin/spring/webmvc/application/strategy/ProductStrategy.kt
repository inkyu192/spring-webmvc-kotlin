package spring.webmvc.application.strategy

import spring.webmvc.application.dto.command.ProductCreateCommand
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.domain.model.enums.Category

interface ProductStrategy {
    fun supports(category: Category): Boolean
    fun findByProductId(productId: Long): ProductResult
    fun createProduct(productCreateCommand: ProductCreateCommand): ProductResult
}