package spring.webmvc.application.event.listener

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionalEventListener
import spring.webmvc.application.event.RecentlyViewedEvent
import spring.webmvc.domain.model.entity.RecentlyViewedProduct
import spring.webmvc.domain.repository.ProductRepository
import spring.webmvc.domain.repository.RecentlyViewedProductRepository
import spring.webmvc.domain.repository.UserRepository

@Component
class RecentlyViewedEventListener(
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
) {
    @Async
    @TransactionalEventListener
    @Transactional
    fun handleRecentlyViewed(event: RecentlyViewedEvent) {
        val existing = recentlyViewedProductRepository.findByUserIdAndProductId(
            event.userId, event.productId
        )

        if (existing != null) {
            existing.updateViewedAt()
        } else {
            val user = userRepository.findById(event.userId) ?: return
            val product = productRepository.findById(event.productId) ?: return
            val newRecord = RecentlyViewedProduct.create(user, product)
            recentlyViewedProductRepository.save(newRecord)
        }
    }
}
