package spring.webmvc.application.service

import io.jsonwebtoken.ExpiredJwtException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.cache.TokenCache
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.infrastructure.config.security.JwtProvider
import spring.webmvc.presentation.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class AuthService(
    private val jwtProvider: JwtProvider,
    private val tokenCache: TokenCache,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun login(account: String, password: String): Pair<String, String> {
        val member = memberRepository.findByAccount(account)
            ?.takeIf { passwordEncoder.matches(password, it.password) }
            ?: throw BadCredentialsException("잘못된 아이디 또는 비밀번호입니다.")

        val memberId = checkNotNull(member.id)

        val accessToken = jwtProvider.createAccessToken(
            memberId = memberId,
            permissions = getPermissions(member)
        )
        val refreshToken = jwtProvider.createRefreshToken()

        tokenCache.set(memberId = memberId, value = refreshToken)

        return accessToken to refreshToken
    }

    fun refreshToken(accessToken: String, refreshToken: String): Pair<String, String> {
        val requestMemberId = extractMemberId(accessToken)
        jwtProvider.parseRefreshToken(refreshToken)

        val member = memberRepository.findByIdOrNull(requestMemberId)
            ?: throw EntityNotFoundException(kClass = Member::class, id = requestMemberId)

        tokenCache.get(requestMemberId)
            ?.takeIf { refreshToken == it }
            ?: throw BadCredentialsException("유효하지 않은 인증 정보입니다. 다시 로그인해 주세요.")

        return jwtProvider.createAccessToken(
            memberId = checkNotNull(member.id),
            permissions = getPermissions(member),
        ) to refreshToken
    }

    private fun extractMemberId(accessToken: String): Long {
        val claims = runCatching { jwtProvider.parseAccessToken(accessToken) }
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