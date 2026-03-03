package spring.webmvc.infrastructure.exception

import org.springframework.http.HttpStatus

class FailedAwsIntegrationException(
    serviceName: String,
    throwable: Throwable,
) : AbstractHttpException(
    httpStatus = HttpStatus.BAD_GATEWAY,
    translationArgs = arrayOf(serviceName),
    throwable = throwable,
)
