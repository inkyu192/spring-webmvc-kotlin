package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import java.time.LocalDate


data class MemberCreateRequest(
    @field:Email
    val email: String,
    val password: String,
    val name: String,
    @field:Pattern(regexp = "^010-\\d{3,4}-\\d{4}$")
    val phone: String,
    val birthDate: LocalDate,
    val roleIds: List<Long> = emptyList(),
    val permissionIds: List<Long> = emptyList(),
)
