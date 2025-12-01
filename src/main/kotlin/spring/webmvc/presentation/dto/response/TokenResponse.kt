package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.TokenResult

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
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
