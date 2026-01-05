package spring.webmvc.domain.repository

import org.springframework.data.domain.Page
import spring.webmvc.application.dto.query.ProductCursorPageQuery
import spring.webmvc.application.dto.query.ProductOffsetPageQuery
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.infrastructure.persistence.dto.CursorPage

interface ProductRepository {
    fun findById(id: Long): Product
    fun findAllById(ids: Iterable<Long>): List<Product>
    fun findAllWithCursorPage(query: ProductCursorPageQuery): CursorPage<Product>
    fun findAllWithOffsetPage(query: ProductOffsetPageQuery): Page<Product>
    fun save(product: Product): Product
    fun delete(product: Product)
}