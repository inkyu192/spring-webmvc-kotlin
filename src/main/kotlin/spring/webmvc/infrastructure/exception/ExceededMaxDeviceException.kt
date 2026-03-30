package spring.webmvc.infrastructure.exception

import org.springframework.http.HttpStatus

class ExceededMaxDeviceException(maxDevices: Int) : AbstractHttpException(
    httpStatus = HttpStatus.CONFLICT,
    messageArgs = arrayOf(maxDevices.toString()),
)
