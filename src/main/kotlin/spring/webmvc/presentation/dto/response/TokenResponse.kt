package spring.webmvc.presentation.dto.response

import spring.webmvc.application.dto.result.TokenResult

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
) {
    constructor(tokenResult: TokenResult) : this(
        accessToken = tokenResult.accessToken,
        refreshToken = tokenResult.refreshToken,
    )
}
