package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.UserCurationProduct

interface UserCurationProductRepository {
    fun findByUserIdAndCurationId(userId: Long, curationId: Long): UserCurationProduct?
}
