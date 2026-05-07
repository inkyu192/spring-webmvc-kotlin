package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.dto.CursorPage
import spring.webmvc.domain.model.entity.RecentlyViewedProduct
import spring.webmvc.domain.repository.RecentlyViewedProductRepository
import spring.webmvc.infrastructure.persistence.jpa.RecentlyViewedProductJpaRepository
import spring.webmvc.infrastructure.persistence.jpa.RecentlyViewedProductQuerydslRepository

@Component
class RecentlyViewedProductRepositoryAdapter(
    private val jpaRepository: RecentlyViewedProductJpaRepository,
    private val querydslRepository: RecentlyViewedProductQuerydslRepository,
) : RecentlyViewedProductRepository {

    override fun findByUserIdAndProductId(userId: Long, productId: Long): RecentlyViewedProduct? =
        jpaRepository.findByUserIdAndProductId(userId, productId)

    override fun findProductIdsByUserIdWithinDays(userId: Long, days: Int): Set<Long> =
        querydslRepository.findProductIdsByUserIdWithinDays(userId, days)

    override fun findAllByUserIdWithCursorPage(
        userId: Long,
        cursorId: Long?,
        days: Int
    ): CursorPage<RecentlyViewedProduct> =
        querydslRepository.findAllByUserIdWithCursorPage(userId, cursorId, days)

    override fun save(recentlyViewedProduct: RecentlyViewedProduct): RecentlyViewedProduct =
        jpaRepository.save(recentlyViewedProduct)
}
