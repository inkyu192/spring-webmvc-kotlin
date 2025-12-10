package spring.webmvc.presentation.exception

class AwsIntegrationException(
    serviceName: String,
    throwable: Throwable,
): AbstractHttpException(
    message = "$serviceName 서비스와의 통신 중 오류가 발생했습니다.",
    httpStatus = org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
    throwable = throwable,
)