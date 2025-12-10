package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Pattern
import spring.webmvc.application.dto.command.UserCreateCommand
import spring.webmvc.application.dto.command.UserUpdateCommand
import spring.webmvc.domain.model.enums.UserType
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import java.time.LocalDate
import jakarta.validation.constraints.Email as EmailValidation

data class UserCreateRequest(
    @field:EmailValidation
    val email: String,
    val password: String,
    val name: String,
    val type: UserType = UserType.CUSTOMER,
    @field:Pattern(regexp = "^010-\\d{3,4}-\\d{4}$")
    val phone: String,
    val birthDate: LocalDate,
    val roleIds: List<Long> = emptyList(),
    val permissionIds: List<Long> = emptyList(),
) {
    fun toCommand() = UserCreateCommand(
        email = Email.create(email),
        password = password,
        name = name,
        phone = Phone.create(phone),
        birthDate = birthDate,
        type = type,
        roleIds = roleIds,
        permissionIds = permissionIds,
    )
}

data class UserUpdateRequest(
    val name: String?,
    @field:Pattern(regexp = "^010-\\d{3,4}-\\d{4}$")
    val phone: String?,
    val birthDate: LocalDate?,
) {
    fun toCommand(id: Long) = UserUpdateCommand(
        userId = id,
        name = name,
        phone = phone?.let { Phone.create(phone) },
        birthDate = birthDate,
    )
}