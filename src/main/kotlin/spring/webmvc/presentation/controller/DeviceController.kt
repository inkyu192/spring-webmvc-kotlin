package spring.webmvc.presentation.controller

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.DeviceService
import spring.webmvc.infrastructure.security.SecurityContextUtil
import spring.webmvc.presentation.dto.response.DeviceListResponse

@RestController
@RequestMapping("/devices")
class DeviceController(
    private val deviceService: DeviceService,
) {
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    fun getMyDevices(): DeviceListResponse {
        val userId = SecurityContextUtil.getUserId()
        return DeviceListResponse.of(deviceService.getMyDevices(userId))
    }

    @DeleteMapping("/{deviceId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteDevice(
        @PathVariable deviceId: String,
    ) {
        val userId = SecurityContextUtil.getUserId()
        deviceService.deleteDevice(userId, deviceId)
    }
}
