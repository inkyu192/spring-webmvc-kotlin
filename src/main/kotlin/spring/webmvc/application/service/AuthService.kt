package spring.webmvc.application.service

import io.jsonwebtoken.ExpiredJwtException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.entity.Token
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.domain.repository.TokenRepository
import spring.webmvc.infrastructure.config.security.JwtTokenProvider
import spring.webmvc.presentation.dto.request.MemberLoginRequest
import spring.webmvc.presentation.dto.request.TokenRequest
import spring.webmvc.presentation.dto.response.TokenResponse
import spring.webmvc.presentation.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class AuthService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun login(memberLoginRequest: MemberLoginRequest): TokenResponse {
        val member = memberRepository.findByAccount(memberLoginRequest.account)
            ?.takeIf { passwordEncoder.matches(memberLoginRequest.password, it.password) }
            ?: throw BadCredentialsException("잘못된 아이디 또는 비밀번호입니다.")

        val memberId = checkNotNull(member.id)

        val accessToken = jwtTokenProvider.createAccessToken(
            memberId = memberId,
            permissions = getPermissions(member)
        )
        val refreshToken = jwtTokenProvider.createRefreshToken()

        tokenRepository.save(
            Token.create(
                memberId = memberId,
                refreshToken = refreshToken,
            )
        )

        return TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }

    fun refreshToken(tokenRequest: TokenRequest): TokenResponse {
        val requestMemberId = extractMemberId(tokenRequest.accessToken)
        jwtTokenProvider.parseRefreshToken(tokenRequest.refreshToken)

        val member = memberRepository.findByIdOrNull(requestMemberId)
            ?: throw EntityNotFoundException(Member::class.java, requestMemberId)

        val token = tokenRepository.findByIdOrNull(requestMemberId)
            ?.takeIf { tokenRequest.refreshToken == it.refreshToken }
            ?: throw BadCredentialsException("유효하지 않은 인증 정보입니다. 다시 로그인해 주세요.")

        return TokenResponse(
            accessToken = jwtTokenProvider.createAccessToken(
                memberId = checkNotNull(member.id),
                permissions = getPermissions(member),
            ),
            refreshToken = token.refreshToken,
        )
    }

    private fun extractMemberId(accessToken: String): Long {
        val claims = runCatching { jwtTokenProvider.parseAccessToken(accessToken) }
            .getOrElse { throwable ->
                when (throwable) {
                    is ExpiredJwtException -> throwable.claims
                    else -> throw throwable
                }
            }

        return claims["memberId"].toString().toLong()
    }

    private fun getPermissions(member: Member): List<String> {
        val rolePermissions = member.memberRoles
            .flatMap { it.role.rolePermissions }
            .map { it.permission.name }

        val directPermissions = member.memberPermissions
            .map { it.permission.name }

        return (rolePermissions + directPermissions).distinct()
    }
}