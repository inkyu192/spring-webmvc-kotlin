package spring.webmvc.application.strategy

import spring.webmvc.application.dto.command.ProductPropertyPutCommand
import spring.webmvc.application.dto.result.ProductPropertyResult
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.enums.ProductCategory

interface ProductPropertyStrategy {
    fun category(): ProductCategory
    fun findByProductId(productId: Long): ProductPropertyResult
    fun create(product: Product, command: ProductPropertyPutCommand): ProductPropertyResult
    fun replace(productId: Long, command: ProductPropertyPutCommand): ProductPropertyResult
    fun deleteProduct(productId: Long)
}
