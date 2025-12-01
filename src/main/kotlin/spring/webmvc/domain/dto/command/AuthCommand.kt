package spring.webmvc.domain.dto.command

import spring.webmvc.domain.model.vo.Email

data class LoginCommand(
    val email: Email,
    val password: String,
)

data class RefreshTokenCommand(
    val accessToken: String,
    val refreshToken: String,
)

data class JoinVerifyRequestCommand(
    val email: Email,
)

data class JoinVerifyConfirmCommand(
    val token: String,
)

data class PasswordResetRequestCommand(
    val email: Email,
)

data class PasswordResetConfirmCommand(
    val token: String,
    val password: String,
)