package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import spring.webmvc.application.dto.command.PasswordResetEmailCommand
import spring.webmvc.application.dto.command.VerifyEmailCommand

data class VerifyEmailRequest(
    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    val verifyLink: String,
) {
    fun toCommand() = VerifyEmailCommand(
        email = email,
        verifyLink = verifyLink,
    )
}

data class PasswordResetEmailRequest(
    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    val resetLink: String,
) {
    fun toCommand() = PasswordResetEmailCommand(
        email = email,
        resetLink = resetLink,
    )
}
