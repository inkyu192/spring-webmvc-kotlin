package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Product
import spring.webmvc.infrastructure.persistence.dto.CursorPage

interface ProductRepository {
    fun findByIdOrNull(id: Long): Product?
    fun findAllById(ids: Iterable<Long>): List<Product>
    fun findAll(nextCursorId: Long?, size: Int, name: String?): CursorPage<Product>
}