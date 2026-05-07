package spring.webmvc.domain.repository

import spring.webmvc.domain.dto.CursorPage
import spring.webmvc.domain.model.entity.RecentlyViewedProduct

interface RecentlyViewedProductRepository {
    fun findByUserIdAndProductId(userId: Long, productId: Long): RecentlyViewedProduct?
    fun findProductIdsByUserIdWithinDays(userId: Long, days: Int = 30): Set<Long>
    fun findAllByUserIdWithCursorPage(userId: Long, cursorId: Long?, days: Int = 30): CursorPage<RecentlyViewedProduct>
    fun save(recentlyViewedProduct: RecentlyViewedProduct): RecentlyViewedProduct
}
