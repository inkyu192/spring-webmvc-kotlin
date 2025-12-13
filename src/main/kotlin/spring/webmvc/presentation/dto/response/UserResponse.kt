package spring.webmvc.presentation.dto.response

import org.springframework.data.domain.Page
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.entity.UserCredential
import spring.webmvc.domain.model.entity.UserOAuth
import spring.webmvc.domain.model.enums.Gender
import spring.webmvc.domain.model.enums.OAuthProvider
import java.time.Instant
import java.time.LocalDate

data class UserListResponse(
    val id: Long,
    val name: String,
    val phone: String,
    val gender: Gender,
    val birthday: LocalDate,
    val createdAt: Instant,
) {
    companion object {
        fun from(user: User): UserListResponse {
            return UserListResponse(
                id = checkNotNull(user.id),
                name = user.name,
                phone = user.phone.value,
                gender = user.gender,
                birthday = user.birthday,
                createdAt = user.createdAt,
            )
        }
    }
}

data class UserDetailResponse(
    val id: Long,
    val name: String,
    val phone: String,
    val gender: Gender,
    val birthday: LocalDate,
    val credential: UserCredentialResponse?,
    val oauths: List<UserOAuthResponse>,
    val createdAt: Instant,
) {
    companion object {
        fun from(
            user: User,
            credential: UserCredential?,
            oauths: List<UserOAuth>,
        ): UserDetailResponse {
            return UserDetailResponse(
                id = checkNotNull(user.id),
                name = user.name,
                phone = user.phone.value,
                gender = user.gender,
                birthday = user.birthday,
                credential = credential?.let { UserCredentialResponse.from(it) },
                oauths = oauths.map { UserOAuthResponse.from(it) },
                createdAt = user.createdAt,
            )
        }
    }
}

data class UserCredentialResponse(
    val email: String,
    val verifiedAt: Instant?,
) {
    companion object {
        fun from(credential: UserCredential): UserCredentialResponse {
            return UserCredentialResponse(
                email = credential.email.value,
                verifiedAt = credential.verifiedAt,
            )
        }
    }
}

data class UserOAuthResponse(
    val provider: OAuthProvider,
    val oauthUserId: String,
) {
    companion object {
        fun from(oauth: UserOAuth): UserOAuthResponse {
            return UserOAuthResponse(
                provider = oauth.oauthProvider,
                oauthUserId = oauth.oauthUserId,
            )
        }
    }
}

data class UserPageResponse(
    val page: OffsetPageResponse,
    val users: List<UserListResponse>,
) {
    companion object {
        fun from(page: Page<User>) = UserPageResponse(
            page = OffsetPageResponse(page),
            users = page.content.map { UserListResponse.from(user = it) },
        )
    }
}
