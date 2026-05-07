package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
class RecentlyViewedProduct protected constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product,
) : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    var viewedAt: Instant = Instant.now()
        protected set

    fun updateViewedAt() {
        viewedAt = Instant.now()
    }

    companion object {
        fun create(user: User, product: Product) = RecentlyViewedProduct(user, product)
    }
}
