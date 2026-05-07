package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.Wishlist

interface WishlistJpaRepository : JpaRepository<Wishlist, Long> {
    fun findByUserIdAndProductId(userId: Long, productId: Long): Wishlist?
}
