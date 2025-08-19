package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Product
import spring.webmvc.infrastructure.persistence.dto.CursorPage

interface ProductRepository {
    fun findById(id: Long): Product?
    fun findByIds(ids: Iterable<Long>): List<Product>
    fun findWithCursorPage(cursorId: Long?, size: Int, name: String?): CursorPage<Product>
}