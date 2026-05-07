package spring.webmvc.infrastructure.persistence.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import spring.webmvc.domain.dto.CursorPage
import spring.webmvc.domain.model.entity.QRecentlyViewedProduct.recentlyViewedProduct
import spring.webmvc.domain.model.entity.RecentlyViewedProduct
import java.time.Instant
import java.time.temporal.ChronoUnit

@Repository
class RecentlyViewedProductQuerydslRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    companion object {
        const val DEFAULT_PAGE_SIZE = 10L
    }

    fun findProductIdsByUserIdWithinDays(userId: Long, days: Int): Set<Long> {
        val cutoffDate = Instant.now().minus(days.toLong(), ChronoUnit.DAYS)

        return jpaQueryFactory
            .select(recentlyViewedProduct.product.id)
            .from(recentlyViewedProduct)
            .where(
                recentlyViewedProduct.user.id.eq(userId),
                recentlyViewedProduct.viewedAt.goe(cutoffDate),
            )
            .fetch()
            .filterNotNull()
            .toSet()
    }

    fun findAllByUserIdWithCursorPage(userId: Long, cursorId: Long?, days: Int): CursorPage<RecentlyViewedProduct> {
        val cutoffDate = Instant.now().minus(days.toLong(), ChronoUnit.DAYS)

        val content = jpaQueryFactory
            .selectFrom(recentlyViewedProduct)
            .innerJoin(recentlyViewedProduct.product).fetchJoin()
            .where(
                recentlyViewedProduct.user.id.eq(userId),
                recentlyViewedProduct.viewedAt.goe(cutoffDate),
                loeCursorId(cursorId),
            )
            .orderBy(recentlyViewedProduct.id.desc())
            .limit(DEFAULT_PAGE_SIZE + 1)
            .fetch()

        return CursorPage.create(content = content, size = DEFAULT_PAGE_SIZE) { it.id }
    }

    private fun loeCursorId(cursorId: Long?) = cursorId?.let { recentlyViewedProduct.id.loe(cursorId) }
}
