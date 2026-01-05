package spring.webmvc.application.strategy

import spring.webmvc.application.dto.command.ProductAttributeCreateCommand
import spring.webmvc.application.dto.command.ProductAttributeUpdateCommand
import spring.webmvc.application.dto.result.ProductAttributeResult
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.Category

interface ProductAttributeStrategy {
    fun category(): Category
    fun findByProductId(productId: Long): ProductAttributeResult
    fun createProduct(product: Product, command: ProductAttributeCreateCommand): ProductAttributeResult
    fun updateProduct(productId: Long, command: ProductAttributeUpdateCommand): ProductAttributeResult
    fun deleteProduct(productId: Long)
}
