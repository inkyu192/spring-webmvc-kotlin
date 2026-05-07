package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.RecentlyViewedProduct

interface RecentlyViewedProductJpaRepository : JpaRepository<RecentlyViewedProduct, Long> {
    fun findByUserIdAndProductId(userId: Long, productId: Long): RecentlyViewedProduct?
}
