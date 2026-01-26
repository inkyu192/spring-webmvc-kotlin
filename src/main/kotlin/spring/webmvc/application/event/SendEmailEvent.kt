package spring.webmvc.application.event

import spring.webmvc.domain.model.vo.Email

data class SendVerifyEmailEvent(
    val email: Email,
)

data class SendPasswordResetEmailEvent(
    val email: Email,
)
