package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.UserDevice

interface UserDeviceRepository {
    fun findByUserIdAndDeviceId(userId: Long, deviceId: String): UserDevice?
    fun findAllByUserId(userId: Long): List<UserDevice>
    fun countByUserId(userId: Long): Long
    fun save(userDevice: UserDevice): UserDevice
    fun delete(userDevice: UserDevice)
}
