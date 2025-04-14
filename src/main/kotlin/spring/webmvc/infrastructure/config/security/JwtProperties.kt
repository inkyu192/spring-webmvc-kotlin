package spring.webmvc.infrastructure.config.security

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "jwt")
class JwtProperties(
    val accessToken: TokenProperties,
    val refreshToken: TokenProperties,
) {
    class TokenProperties(
        val key: String,
        val expiration: Duration,
    )
}