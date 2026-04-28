package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.UserProductBadge

interface UserProductBadgeRepository {
    fun findByUserIdAndProductId(userId: Long, productId: Long): UserProductBadge?
    fun findByUserIdAndProductIds(userId: Long, productIds: List<Long>): List<UserProductBadge>
}
