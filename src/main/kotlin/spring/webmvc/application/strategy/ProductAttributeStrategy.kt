package spring.webmvc.application.strategy

import spring.webmvc.application.dto.command.ProductAttributePutCommand
import spring.webmvc.application.dto.result.ProductAttributeResult
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.ProductCategory

interface ProductAttributeStrategy {
    fun category(): ProductCategory
    fun findByProductId(productId: Long): ProductAttributeResult
    fun create(product: Product, command: ProductAttributePutCommand): ProductAttributeResult
    fun update(productId: Long, command: ProductAttributePutCommand): ProductAttributeResult
    fun deleteProduct(productId: Long)
}
