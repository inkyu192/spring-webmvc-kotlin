package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.ProductTag

interface ProductTagJpaRepository : JpaRepository<ProductTag, Long> {
    fun findByProductId(productId: Long): List<ProductTag>
}
