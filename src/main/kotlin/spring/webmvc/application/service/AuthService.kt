package spring.webmvc.application.service

import io.jsonwebtoken.ExpiredJwtException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.result.TokenResult
import spring.webmvc.application.event.SendPasswordResetEmailEvent
import spring.webmvc.application.event.SendVerifyEmailEvent
import spring.webmvc.domain.dto.command.*
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.enums.MemberStatus
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.domain.repository.cache.AuthCacheRepository
import spring.webmvc.domain.repository.cache.TokenCacheRepository
import spring.webmvc.infrastructure.security.JwtProvider

@Service
@Transactional(readOnly = true)
class AuthService(
    private val jwtProvider: JwtProvider,
    private val tokenCacheRepository: TokenCacheRepository,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authCacheRepository: AuthCacheRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional
    fun login(command: LoginCommand): TokenResult {
        val member = memberRepository.findByEmail(command.email)
            ?: throw BadCredentialsException("유효하지 않은 인증 정보입니다.")

        if (!passwordEncoder.matches(command.password, member.password)) {
            throw BadCredentialsException("유효하지 않은 인증 정보입니다.")
        }

        if (member.isNotActive()) {
            throw DisabledException("계정이 활성화되지 않았습니다.")
        }

        val memberId = checkNotNull(member.id)

        val accessToken = jwtProvider.createAccessToken(
            memberId = memberId,
            permissions = getPermissions(member)
        )
        val refreshToken = jwtProvider.createRefreshToken()

        tokenCacheRepository.setRefreshToken(memberId = memberId, refreshToken = refreshToken)

        return TokenResult(accessToken = accessToken, refreshToken = refreshToken)
    }

    fun refreshToken(command: RefreshTokenCommand): TokenResult {
        val memberId = extractMemberId(command.accessToken)
        jwtProvider.parseRefreshToken(command.refreshToken)

        val member = memberRepository.findById(memberId)

        if (tokenCacheRepository.getRefreshToken(memberId) != command.refreshToken) {
            throw BadCredentialsException("유효하지 않은 인증 정보입니다.")
        }

        return TokenResult(
            accessToken = jwtProvider.createAccessToken(
                memberId = memberId,
                permissions = getPermissions(member),
            ),
            refreshToken = command.refreshToken,
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

    fun requestJoinVerify(command: JoinVerifyRequestCommand) {
        eventPublisher.publishEvent(SendVerifyEmailEvent(email = command.email))
    }

    @Transactional
    fun confirmJoinVerify(command: JoinVerifyConfirmCommand) {
        val email = authCacheRepository.getJoinVerifyToken(command.token)
            ?: throw BadCredentialsException("유효하지 않은 인증 정보입니다.")

        val member = memberRepository.findByEmail(Email.create(email))
            ?: throw BadCredentialsException("유효하지 않은 인증 정보입니다.")

        member.updateStatus(MemberStatus.ACTIVE)

        authCacheRepository.deleteJoinVerifyToken(command.token)
    }

    fun requestPasswordReset(command: PasswordResetRequestCommand) {
        memberRepository.findByEmail(command.email) ?: throw BadCredentialsException("유효하지 않은 인증 정보입니다.")

        eventPublisher.publishEvent(SendPasswordResetEmailEvent(email = command.email))
    }

    @Transactional
    fun confirmPasswordReset(command: PasswordResetConfirmCommand) {
        val email = authCacheRepository.getPasswordResetToken(command.token)
            ?: throw BadCredentialsException("유효하지 않은 인증 정보입니다.")

        val member = memberRepository.findByEmail(Email.create(email))
            ?: throw BadCredentialsException("유효하지 않은 인증 정보입니다.")

        member.updatePassword(passwordEncoder.encode(command.password))

        authCacheRepository.deletePasswordResetToken(command.token)
    }
}