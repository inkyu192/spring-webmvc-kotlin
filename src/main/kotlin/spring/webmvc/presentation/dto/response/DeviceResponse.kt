package spring.webmvc.presentation.dto.response

import spring.webmvc.domain.model.entity.UserDevice
import java.time.Instant

data class DeviceListResponse(
    val size: Long,
    val devices: List<DeviceResponse>,
) {
    companion object {
        fun of(userDevices: List<UserDevice>) = DeviceListResponse(
            size = userDevices.size.toLong(),
            devices = userDevices.map { DeviceResponse.of(it) },
        )
    }
}

data class DeviceResponse(
    val deviceId: String,
    val deviceName: String,
    val lastLoginAt: Instant,
    val createdAt: Instant,
) {
    companion object {
        fun of(userDevice: UserDevice) = DeviceResponse(
            deviceId = userDevice.deviceId,
            deviceName = userDevice.deviceName,
            lastLoginAt = userDevice.lastLoginAt,
            createdAt = userDevice.createdAt,
        )
    }
}
