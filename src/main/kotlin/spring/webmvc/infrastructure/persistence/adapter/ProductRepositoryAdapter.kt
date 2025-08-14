package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.infrastructure.persistence.jpa.ProductJpaRepository
import spring.webmvc.infrastructure.persistence.jpa.ProductQuerydslRepository

@Component
class ProductRepositoryAdapter(
    private val jpaRepository: ProductJpaRepository,
    private val querydslRepository: ProductQuerydslRepository,
) : ProductRepository {
    override fun findByIdOrNull(id: Long) = jpaRepository.findByIdOrNull(id)

    override fun findAllById(ids: Iterable<Long>) = jpaRepository.findAllById(ids)

    override fun findAll(nextCursorId: Long?, size: Int, name: String?) =
        querydslRepository.findAll(nextCursorId = nextCursorId, size = size, name = name)
}