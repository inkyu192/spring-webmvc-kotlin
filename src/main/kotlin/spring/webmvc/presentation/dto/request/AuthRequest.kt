package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.NotBlank
import spring.webmvc.domain.dto.command.*
import spring.webmvc.domain.model.vo.Email
import jakarta.validation.constraints.Email as EmailValidation

data class LoginRequest(
    @field:EmailValidation
    val email: String,
    val password: String,
) {
    fun toCommand() = LoginCommand(
        email = Email.create(email),
        password = password,
    )
}

data class TokenRequest(
    val accessToken: String,
    val refreshToken: String,
) {
    fun toCommand() = RefreshTokenCommand(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
}

data class JoinVerifyRequest(
    @field:EmailValidation
    val email: String,
) {
    fun toCommand() = JoinVerifyRequestCommand(
        email = Email.create(email),
    )
}

data class JoinVerifyConfirmRequest(
    @field:NotBlank
    val token: String,
) {
    fun toCommand() = JoinVerifyConfirmCommand(
        token = token,
    )
}

data class PasswordResetRequest(
    @field:EmailValidation
    val email: String,
) {
    fun toCommand() = PasswordResetRequestCommand(
        email = Email.create(email),
    )
}

data class PasswordResetConfirmRequest(
    @field:NotBlank
    val token: String,
    @field:NotBlank
    val password: String,
) {
    fun toCommand() = PasswordResetConfirmCommand(
        token = token,
        password = password,
    )
}