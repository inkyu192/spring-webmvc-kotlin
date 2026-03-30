package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.model.entity.UserDevice
import spring.webmvc.domain.repository.UserDeviceRepository
import spring.webmvc.domain.repository.cache.TokenCacheRepository
import spring.webmvc.infrastructure.exception.NotFoundEntityException

@Service
@Transactional(readOnly = true)
class DeviceService(
    private val userDeviceRepository: UserDeviceRepository,
    private val tokenCacheRepository: TokenCacheRepository,
) {
    fun getMyDevices(userId: Long): List<UserDevice> =
        userDeviceRepository.findAllByUserId(userId)

    @Transactional
    fun deleteDevice(userId: Long, deviceId: String) {
        val userDevice = userDeviceRepository.findByUserIdAndDeviceId(userId, deviceId)
            ?: throw NotFoundEntityException(kClass = UserDevice::class, id = deviceId)

        userDeviceRepository.delete(userDevice)
        tokenCacheRepository.removeRefreshToken(userId = userId, deviceId = deviceId)
    }
}
