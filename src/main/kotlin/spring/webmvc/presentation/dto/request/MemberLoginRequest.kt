package spring.webmvc.presentation.dto.request

import jakarta.validation.constraints.Email

data class MemberLoginRequest(
    @field:Email
    val email: String,
    val password: String,
)
