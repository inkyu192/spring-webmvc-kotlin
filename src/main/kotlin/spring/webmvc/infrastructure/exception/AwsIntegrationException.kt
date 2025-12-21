package spring.webmvc.infrastructure.exception

import org.springframework.http.HttpStatus

class AwsIntegrationException(
    serviceName: String,
    throwable: Throwable,
) : AbstractHttpException(
    message = "$serviceName 서비스와의 통신 중 오류가 발생했습니다.",
    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    throwable = throwable,
)