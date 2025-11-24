package spring.webmvc.domain.dto.command

import spring.webmvc.domain.model.enums.MemberStatus
import spring.webmvc.domain.model.enums.MemberType
import java.time.LocalDate

data class MemberCreateCommand(
    val email: String,
    val password: String,
    val name: String,
    val phone: String,
    val birthDate: LocalDate,
    val memberType: MemberType,
    val roleIds: List<Long>,
    val permissionIds: List<Long>,
)

data class MemberUpdateCommand(
    val memberId: Long,
    val name: String?,
    val phone: String?,
    val birthDate: LocalDate?,
)

data class MemberStatusUpdateCommand(
    val memberId: Long,
    val status: MemberStatus,
)

data class PasswordChangeCommand(
    val memberId: Long,
    val currentPassword: String,
    val newPassword: String,
)
