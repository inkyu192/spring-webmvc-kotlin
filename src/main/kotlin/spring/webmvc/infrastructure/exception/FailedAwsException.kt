package spring.webmvc.infrastructure.exception

class FailedAwsException(
    serviceName: String,
    throwable: Throwable,
) : AbstractExternalException(
    messageArgs = arrayOf(serviceName),
    throwable = throwable,
)
