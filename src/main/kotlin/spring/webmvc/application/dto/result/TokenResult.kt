package spring.webmvc.application.dto.result

data class TokenResult(
    val accessToken: String,
    val refreshToken: String,
)
