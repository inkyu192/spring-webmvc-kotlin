package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.TokenResult
import spring.webmvc.domain.model.entity.User

data class SignUpResponse(
    val id: Long,
) {
    companion object {
        fun from(user: User): SignUpResponse {
            return SignUpResponse(
                id = checkNotNull(user.id),
            )
        }
    }
}

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
) {
    companion object {
        fun from(tokenResult: TokenResult): TokenResponse {
            return TokenResponse(
                accessToken = tokenResult.accessToken,
                refreshToken = tokenResult.refreshToken,
            )
        }
    }
}