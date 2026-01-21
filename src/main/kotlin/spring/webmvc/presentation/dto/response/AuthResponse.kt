package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.TokenResult
import spring.webmvc.domain.model.entity.User

data class SignUpResponse(
    val id: Long,
    val profileImage: String?,
) {
    companion object {
        fun of(user: User, cloudfrontDomain: String): SignUpResponse {
            return SignUpResponse(
                id = checkNotNull(user.id),
                profileImage = user.profileImage?.let { "$cloudfrontDomain/$it" },
            )
        }
    }
}

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
) {
    companion object {
        fun of(tokenResult: TokenResult): TokenResponse {
            return TokenResponse(
                accessToken = tokenResult.accessToken,
                refreshToken = tokenResult.refreshToken,
            )
        }
    }
}