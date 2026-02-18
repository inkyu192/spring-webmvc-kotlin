package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Email
import spring.webmvc.application.dto.command.PasswordResetEmailCommand
import spring.webmvc.application.dto.command.VerifyEmailCommand

data class VerifyEmailRequest(
    @field:Email
    val email: String,
    val verifyLink: String,
) {
    fun toCommand() = VerifyEmailCommand(
        email = email,
        verifyLink = verifyLink,
    )
}

data class PasswordResetEmailRequest(
    @field:Email
    val email: String,
    val resetLink: String,
) {
    fun toCommand() = PasswordResetEmailCommand(
        email = email,
        resetLink = resetLink,
    )
}
