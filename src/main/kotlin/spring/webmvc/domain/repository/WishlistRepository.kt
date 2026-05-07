package spring.webmvc.domain.repository

import spring.webmvc.domain.dto.CursorPage
import spring.webmvc.domain.model.entity.Wishlist

interface WishlistRepository {
    fun findById(id: Long): Wishlist?
    fun findByUserIdAndProductId(userId: Long, productId: Long): Wishlist?
    fun findProductIdsByUserId(userId: Long): Set<Long>
    fun findAllByUserIdWithCursorPage(userId: Long, cursorId: Long?): CursorPage<Wishlist>
    fun save(wishlist: Wishlist): Wishlist
    fun delete(wishlist: Wishlist)
}
