package spring.webmvc.application.dto.result

import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.entity.UserCredential
import spring.webmvc.domain.model.entity.UserOAuth

data class UserCredentialResult(
    val user: User,
    val credential: UserCredential?,
    val oauths: List<UserOAuth>,
)