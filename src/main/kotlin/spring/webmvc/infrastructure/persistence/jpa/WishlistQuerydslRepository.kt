package spring.webmvc.infrastructure.persistence.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import spring.webmvc.domain.dto.CursorPage
import spring.webmvc.domain.model.entity.QWishlist.wishlist
import spring.webmvc.domain.model.entity.Wishlist

@Repository
class WishlistQuerydslRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    companion object {
        const val DEFAULT_PAGE_SIZE = 10L
    }

    fun findProductIdsByUserId(userId: Long): Set<Long> {
        return jpaQueryFactory
            .select(wishlist.product.id)
            .from(wishlist)
            .where(wishlist.user.id.eq(userId))
            .fetch()
            .filterNotNull()
            .toSet()
    }

    fun findAllByUserIdWithCursorPage(userId: Long, cursorId: Long?): CursorPage<Wishlist> {
        val content = jpaQueryFactory
            .selectFrom(wishlist)
            .innerJoin(wishlist.product).fetchJoin()
            .where(
                wishlist.user.id.eq(userId),
                loeCursorId(cursorId),
            )
            .orderBy(wishlist.id.desc())
            .limit(DEFAULT_PAGE_SIZE + 1)
            .fetch()

        return CursorPage.create(content = content, size = DEFAULT_PAGE_SIZE) { it.id }
    }

    private fun loeCursorId(cursorId: Long?) = cursorId?.let { wishlist.id.loe(cursorId) }
}
