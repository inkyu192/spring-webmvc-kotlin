package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.result.WishlistResult
import spring.webmvc.domain.dto.CursorPage
import spring.webmvc.domain.model.entity.Product
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.entity.Wishlist
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.domain.repository.UserRepository
import spring.webmvc.domain.repository.WishlistRepository
import spring.webmvc.infrastructure.exception.NotFoundEntityException

@Service
@Transactional(readOnly = true)
class WishlistService(
    private val wishlistRepository: WishlistRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
) {
    fun findWishlists(userId: Long, cursorId: Long?): CursorPage<WishlistResult> {
        val page = wishlistRepository.findAllByUserIdWithCursorPage(userId, cursorId)
        return page.map { WishlistResult.of(it) }
    }

    @Transactional
    fun addWishlist(userId: Long, productId: Long) {
        val existingWishlist = wishlistRepository.findByUserIdAndProductId(userId, productId)
        if (existingWishlist != null) {
            return
        }

        val user = userRepository.findById(userId)
            ?: throw NotFoundEntityException(kClass = User::class, id = userId)
        val product = productRepository.findById(productId)
            ?: throw NotFoundEntityException(kClass = Product::class, id = productId)

        val wishlist = Wishlist.create(user = user, product = product)
        wishlistRepository.save(wishlist)
    }

    @Transactional
    fun removeWishlist(userId: Long, wishlistId: Long) {
        val wishlist = wishlistRepository.findById(wishlistId)
            ?: throw NotFoundEntityException(kClass = Wishlist::class, id = wishlistId)

        if (wishlist.user.id != userId) {
            throw NotFoundEntityException(kClass = Wishlist::class, id = wishlistId)
        }

        wishlistRepository.delete(wishlist)
    }
}
