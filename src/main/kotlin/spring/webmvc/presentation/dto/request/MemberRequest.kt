package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Pattern
import org.springframework.data.domain.Pageable
import spring.webmvc.application.dto.query.MemberSearchQuery
import spring.webmvc.domain.dto.command.MemberCreateCommand
import spring.webmvc.domain.dto.command.MemberStatusUpdateCommand
import spring.webmvc.domain.dto.command.MemberUpdateCommand
import spring.webmvc.domain.dto.command.PasswordChangeCommand
import spring.webmvc.domain.model.enums.MemberStatus
import spring.webmvc.domain.model.enums.MemberType
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import java.time.Instant
import java.time.LocalDate
import jakarta.validation.constraints.Email as EmailValidation

data class MemberCreateRequest(
    @field:EmailValidation
    val email: String,
    val password: String,
    val name: String,
    @field:Pattern(regexp = "^010-\\d{3,4}-\\d{4}$")
    val phone: String,
    val birthDate: LocalDate,
    val roleIds: List<Long> = emptyList(),
    val permissionIds: List<Long> = emptyList(),
) {
    fun toCommand(memberType: MemberType) = MemberCreateCommand(
        email = Email.create(email),
        password = password,
        name = name,
        phone = Phone.create(phone),
        birthDate = birthDate,
        memberType = memberType,
        roleIds = roleIds,
        permissionIds = permissionIds,
    )
}

data class MemberUpdateRequest(
    val name: String?,
    @field:Pattern(regexp = "^010-\\d{3,4}-\\d{4}$")
    val phone: String?,
    val birthDate: LocalDate?,
) {
    fun toCommand(memberId: Long) = MemberUpdateCommand(
        memberId = memberId,
        name = name,
        phone = phone?.let { Phone.create(phone) },
        birthDate = birthDate,
    )
}

data class MemberStatusUpdateRequest(
    val status: MemberStatus,
) {
    fun toCommand(memberId: Long) = MemberStatusUpdateCommand(
        memberId = memberId,
        status = status,
    )
}

data class PasswordChangeRequest(
    val currentPassword: String,
    val newPassword: String,
) {
    fun toCommand(memberId: Long) = PasswordChangeCommand(
        memberId = memberId,
        oldPassword = currentPassword,
        newPassword = newPassword,
    )
}

data class MemberSearchRequest(
    val email: String?,
    val phone: String?,
    val name: String?,
    val status: MemberStatus?,
    val createdFrom: Instant,
    val createdTo: Instant,
) {
    fun toQuery(pageable: Pageable) = MemberSearchQuery(
        pageable = pageable,
        email = email?.let { Email.create(it) },
        phone = phone?.let { Phone.create(it) },
        name = name,
        status = status,
        createdFrom = createdFrom,
        createdTo = createdTo,
    )
}