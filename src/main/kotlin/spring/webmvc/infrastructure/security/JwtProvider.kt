package spring.webmvc.infrastructure.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import spring.webmvc.infrastructure.properties.AppProperties
import java.util.*

@Component
class JwtProvider(
    appProperties: AppProperties,
) {
    private val accessTokenKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(appProperties.jwt.accessToken.key))
    private val accessTokenExpirationTime = appProperties.jwt.accessToken.expiration.toMillis()
    private val refreshTokenKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(appProperties.jwt.refreshToken.key))
    private val refreshTokenExpirationTime = appProperties.jwt.refreshToken.expiration.toMillis()

    fun createAccessToken(userId: Long, permissions: List<String>): String =
        Jwts.builder()
            .claim("userId", userId)
            .claim("permissions", permissions)
            .issuedAt(Date())
            .expiration(Date(Date().time + accessTokenExpirationTime))
            .signWith(accessTokenKey)
            .compact()

    fun createRefreshToken(): String =
        Jwts.builder()
            .issuedAt(Date())
            .expiration(Date(Date().time + refreshTokenExpirationTime))
            .signWith(refreshTokenKey)
            .compact()

    fun parseAccessToken(token: String): Claims =
        Jwts.parser()
            .verifyWith(accessTokenKey)
            .build()
            .parseSignedClaims(token)
            .payload

    fun parseRefreshToken(token: String): Claims =
        Jwts.parser()
            .verifyWith(refreshTokenKey)
            .build()
            .parseSignedClaims(token)
            .payload
}