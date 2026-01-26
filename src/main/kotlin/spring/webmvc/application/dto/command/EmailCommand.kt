package spring.webmvc.application.dto.command

data class VerifyEmailCommand(
    val email: String,
    val verifyLink: String,
)

data class PasswordResetEmailCommand(
    val email: String,
    val resetLink: String,
)
