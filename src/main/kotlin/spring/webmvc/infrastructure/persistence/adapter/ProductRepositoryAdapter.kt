package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.application.dto.query.ProductCursorPageQuery
import spring.webmvc.application.dto.query.ProductOffsetPageQuery
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

    override fun findAllById(ids: Iterable<Long>): List<Product> = jpaRepository.findAllById(ids)

    override fun findAllWithCursorPage(query: ProductCursorPageQuery) =
        querydslRepository.findAllWithCursorPage(query = query)

    override fun findAllWithOffsetPage(query: ProductOffsetPageQuery) =
        querydslRepository.findAllWithOffsetPage(query = query)

    override fun save(product: Product): Product = jpaRepository.save(product)

    override fun delete(product: Product) = jpaRepository.delete(product)
}