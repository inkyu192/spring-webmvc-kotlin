package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Tag
import spring.webmvc.domain.repository.ProductTagRepository
import spring.webmvc.infrastructure.persistence.jpa.ProductTagJpaRepository

@Component
class ProductTagRepositoryAdapter(
    private val jpaRepository: ProductTagJpaRepository,
) : ProductTagRepository {
    override fun findTagsByProductId(productId: Long): List<Tag> =
        jpaRepository.findByProductId(productId).map { it.tag }
}
