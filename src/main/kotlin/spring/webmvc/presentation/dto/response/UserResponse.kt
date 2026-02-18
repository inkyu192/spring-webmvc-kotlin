package spring.webmvc.presentation.dto.response

import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.entity.UserCredential
import spring.webmvc.domain.model.entity.UserOAuth
import spring.webmvc.domain.model.enums.Gender
import spring.webmvc.domain.model.enums.OauthProvider
import java.time.Instant
import java.time.LocalDate

data class UserSummaryResponse(
    val id: Long,
    val name: String,
    val phone: String,
    val gender: Gender,
    val birthday: LocalDate,
    val createdAt: Instant,
) {
    companion object {
        fun of(user: User) = UserSummaryResponse(
            id = checkNotNull(user.id),
            name = user.name,
            phone = user.phone.value,
            gender = user.gender,
            birthday = user.birthday,
            createdAt = user.createdAt,
        )
    }
}

data class UserDetailResponse(
    val id: Long,
    val name: String,
    val phone: String,
    val gender: Gender,
    val birthday: LocalDate,
    val credential: UserCredentialResponse?,
    val oAuths: List<UserOAuthResponse>,
    val createdAt: Instant,
) {
    companion object {
        fun of(
            user: User,
            credential: UserCredential?,
            oAuths: List<UserOAuth>,
        ) = UserDetailResponse(
            id = checkNotNull(user.id),
            name = user.name,
            phone = user.phone.value,
            gender = user.gender,
            birthday = user.birthday,
            credential = credential?.let { UserCredentialResponse.of(it) },
            oAuths = oAuths.map { UserOAuthResponse.of(it) },
            createdAt = user.createdAt,
        )
    }
}

data class UserCredentialResponse(
    val email: String,
    val verifiedAt: Instant?,
) {
    companion object {
        fun of(credential: UserCredential): UserCredentialResponse {
            return UserCredentialResponse(
                email = credential.email.value,
                verifiedAt = credential.verifiedAt,
            )
        }
    }
}

data class UserOAuthResponse(
    val provider: OauthProvider,
    val oauthUserId: String,
) {
    companion object {
        fun of(oauth: UserOAuth) = UserOAuthResponse(
            provider = oauth.oauthProvider,
            oauthUserId = oauth.oauthUserId,
        )
    }
}
