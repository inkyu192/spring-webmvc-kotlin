package spring.webmvc.infrastructure.config.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider(
    jwtProperties: JwtProperties,
) {
    private val accessTokenKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.accessToken.key))
    private val accessTokenExpirationTime = jwtProperties.accessToken.expiration.toMillis()
    private val refreshTokenKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.refreshToken.key))
    private val refreshTokenExpirationTime = jwtProperties.refreshToken.expiration.toMillis()

    fun createAccessToken(memberId: Long, permissions: List<String>): String =
        Jwts.builder()
            .claim("memberId", memberId)
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