package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.infrastructure.extensions.findByIdOrThrow
import spring.webmvc.infrastructure.persistence.jpa.ProductJpaRepository
import spring.webmvc.infrastructure.persistence.jpa.ProductQuerydslRepository

@Component
class ProductRepositoryAdapter(
    private val jpaRepository: ProductJpaRepository,
    private val querydslRepository: ProductQuerydslRepository,
) : ProductRepository {
    override fun findById(id: Long): Product = jpaRepository.findByIdOrThrow(id)

    override fun findAllById(ids: Iterable<Long>) = jpaRepository.findAllById(ids)

    override fun findWithCursorPage(cursorId: Long?, size: Int, name: String?) =
        querydslRepository.findAll(cursorId = cursorId, size = size, name = name)
}