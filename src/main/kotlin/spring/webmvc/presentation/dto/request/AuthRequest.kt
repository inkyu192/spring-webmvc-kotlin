package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Pattern
import spring.webmvc.application.dto.command.*
import spring.webmvc.domain.model.enums.Gender
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import java.time.LocalDate
import jakarta.validation.constraints.Email as EmailValidation

data class SignUpRequest(
    @field:EmailValidation
    val email: String,
    val password: String,
    val name: String,
    val gender: Gender,
    @field:Pattern(regexp = "^010-\\d{3,4}-\\d{4}$")
    val phone: String,
    val birthday: LocalDate,
    val profileImageKey: String?,
    val roleIds: List<Long> = emptyList(),
    val permissionIds: List<Long> = emptyList(),
) {
    fun toCommand() = SignUpCommand(
        email = Email.create(email),
        password = password,
        name = name,
        phone = Phone.create(phone),
        gender = gender,
        birthday = birthday,
        profileImageKey = profileImageKey,
        roleIds = roleIds,
        permissionIds = permissionIds,
    )
}

data class SignInRequest(
    @field:EmailValidation
    val email: String,
    val password: String,
) {
    fun toCommand() = SignInCommand(
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
    val token: String,
    val password: String,
) {
    fun toCommand() = PasswordResetConfirmCommand(
        token = token,
        password = password,
    )
}
