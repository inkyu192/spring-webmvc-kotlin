package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.infrastructure.persistence.ProductJpaRepository
import spring.webmvc.infrastructure.persistence.ProductQuerydslRepository

@Component
class ProductRepositoryAdapter(
    private val jpaRepository: ProductJpaRepository,
    private val querydslRepository: ProductQuerydslRepository,
) : ProductRepository {
    override fun findByIdOrNull(id: Long) = jpaRepository.findByIdOrNull(id)

    override fun findAll(pageable: Pageable, name: String?) =
        querydslRepository.findAll(pageable = pageable, name = name)

    override fun findAllById(ids: Iterable<Long>) = jpaRepository.findAllById(ids)
}