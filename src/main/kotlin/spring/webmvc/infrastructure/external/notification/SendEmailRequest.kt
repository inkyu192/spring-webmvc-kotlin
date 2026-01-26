package spring.webmvc.infrastructure.external.notification

data class VerifyEmailRequest(
    val email: String,
    val verifyLink: String,
)

data class PasswordResetEmailRequest(
    val email: String,
    val resetLink: String,
)
