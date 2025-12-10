package spring.webmvc.application.dto.command

import spring.webmvc.domain.model.enums.UserStatus
import spring.webmvc.domain.model.enums.UserType
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import java.time.LocalDate

data class UserCreateCommand(
    val email: Email,
    val password: String,
    val name: String,
    val phone: Phone,
    val birthDate: LocalDate,
    val type: UserType,
    val roleIds: List<Long>,
    val permissionIds: List<Long>,
)

data class UserUpdateCommand(
    val userId: Long,
    val name: String?,
    val phone: Phone?,
    val birthDate: LocalDate?,
)

data class UserStatusUpdateCommand(
    val id: Long,
    val status: UserStatus,
)