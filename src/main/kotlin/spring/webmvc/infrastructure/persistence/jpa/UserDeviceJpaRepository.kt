package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.UserDevice

interface UserDeviceJpaRepository : JpaRepository<UserDevice, Long> {
    fun findByUserIdAndDeviceId(userId: Long, deviceId: String): UserDevice?
    fun findAllByUserId(userId: Long): List<UserDevice>
    fun countByUserId(userId: Long): Long
    fun findAllByTokenNotNull(): List<UserDevice>
}
