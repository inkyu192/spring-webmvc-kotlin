package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import spring.webmvc.domain.dto.CursorPage
import spring.webmvc.domain.model.entity.Wishlist
import spring.webmvc.domain.repository.WishlistRepository
import spring.webmvc.infrastructure.persistence.jpa.WishlistJpaRepository
import spring.webmvc.infrastructure.persistence.jpa.WishlistQuerydslRepository

@Component
class WishlistRepositoryAdapter(
    private val jpaRepository: WishlistJpaRepository,
    private val querydslRepository: WishlistQuerydslRepository,
) : WishlistRepository {

    override fun findById(id: Long): Wishlist? =
        jpaRepository.findByIdOrNull(id)

    override fun findByUserIdAndProductId(userId: Long, productId: Long): Wishlist? =
        jpaRepository.findByUserIdAndProductId(userId, productId)

    override fun findProductIdsByUserId(userId: Long): Set<Long> =
        querydslRepository.findProductIdsByUserId(userId)

    override fun findAllByUserIdWithCursorPage(userId: Long, cursorId: Long?): CursorPage<Wishlist> =
        querydslRepository.findAllByUserIdWithCursorPage(userId, cursorId)

    override fun save(wishlist: Wishlist): Wishlist =
        jpaRepository.save(wishlist)

    override fun delete(wishlist: Wishlist) =
        jpaRepository.delete(wishlist)
}
