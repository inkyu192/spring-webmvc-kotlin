package spring.webmvc.application.event.listener

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener
import spring.webmvc.application.event.ProductViewEvent
import spring.webmvc.domain.repository.cache.ProductCacheRepository

@Component
class ProductEventListener(
    private val productCacheRepository: ProductCacheRepository,
) {
    @Async
    @TransactionalEventListener
    fun handleProductView(event: ProductViewEvent) {
        productCacheRepository.incrementProductViewCount(productId = event.productId, delta = 1)
    }
}
