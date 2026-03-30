package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.UserDevice
import spring.webmvc.domain.repository.UserDeviceRepository
import spring.webmvc.infrastructure.persistence.jpa.UserDeviceJpaRepository

@Component
class UserDeviceRepositoryAdapter(
    private val jpaRepository: UserDeviceJpaRepository,
) : UserDeviceRepository {
    override fun findByUserIdAndDeviceId(userId: Long, deviceId: String): UserDevice? =
        jpaRepository.findByUserIdAndDeviceId(userId, deviceId)

    override fun findAllByUserId(userId: Long): List<UserDevice> =
        jpaRepository.findAllByUserId(userId)

    override fun countByUserId(userId: Long): Long =
        jpaRepository.countByUserId(userId)

    override fun save(userDevice: UserDevice): UserDevice =
        jpaRepository.save(userDevice)

    override fun delete(userDevice: UserDevice) =
        jpaRepository.delete(userDevice)
}
