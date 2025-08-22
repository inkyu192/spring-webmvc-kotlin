package spring.webmvc.application.service

import io.jsonwebtoken.ExpiredJwtException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.result.TokenResult
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.domain.repository.cache.TokenCacheRepository
import spring.webmvc.infrastructure.security.JwtProvider
import spring.webmvc.presentation.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class AuthService(
    private val jwtProvider: JwtProvider,
    private val tokenCacheRepository: TokenCacheRepository,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional
    fun login(email: String, password: String): TokenResult {
        val member = memberRepository.findByEmail(Email.create(email))
            ?.takeIf { passwordEncoder.matches(password, it.password) }
            ?: throw BadCredentialsException("잘못된 아이디 또는 비밀번호입니다.")

        val memberId = checkNotNull(member.id)

        val accessToken = jwtProvider.createAccessToken(
            memberId = memberId,
            permissions = getPermissions(member)
        )
        val refreshToken = jwtProvider.createRefreshToken()

        tokenCacheRepository.setRefreshToken(memberId = memberId, refreshToken = refreshToken)

        return TokenResult(accessToken = accessToken, refreshToken = refreshToken)
    }

    fun refreshToken(accessToken: String, refreshToken: String): TokenResult {
        val memberId = extractMemberId(accessToken)
        jwtProvider.parseRefreshToken(refreshToken)

        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw EntityNotFoundException(kClass = Member::class, id = memberId)


        if (!tokenCacheRepository.getRefreshToken(memberId).equals(refreshToken)) {
            throw BadCredentialsException("유효하지 않은 인증 정보입니다. 다시 로그인해 주세요.")
        }

        return TokenResult(
            accessToken = jwtProvider.createAccessToken(
                memberId = memberId,
                permissions = getPermissions(member),
            ),
            refreshToken = refreshToken,
        )
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