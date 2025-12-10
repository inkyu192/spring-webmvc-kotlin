package spring.webmvc.application.dto.command

import spring.webmvc.domain.model.enums.UserType
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import java.time.LocalDate

data class SignUpCommand(
    val email: Email,
    val password: String,
    val name: String,
    val phone: Phone,
    val birthDate: LocalDate,
    val type: UserType,
    val roleIds: List<Long>,
    val permissionIds: List<Long>,
)

data class SignInCommand(
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