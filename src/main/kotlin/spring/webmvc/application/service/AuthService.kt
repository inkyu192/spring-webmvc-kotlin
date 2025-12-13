package spring.webmvc.application.service

import io.jsonwebtoken.ExpiredJwtException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.command.*
import spring.webmvc.application.dto.result.TokenResult
import spring.webmvc.application.event.SendPasswordResetEmailEvent
import spring.webmvc.application.event.SendVerifyEmailEvent
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.entity.UserCredential
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.repository.PermissionRepository
import spring.webmvc.domain.repository.RoleRepository
import spring.webmvc.domain.repository.UserCredentialRepository
import spring.webmvc.domain.repository.UserRepository
import spring.webmvc.domain.repository.cache.AuthCacheRepository
import spring.webmvc.domain.repository.cache.TokenCacheRepository
import spring.webmvc.infrastructure.exception.DuplicateEntityException
import spring.webmvc.infrastructure.security.JwtProvider

@Service
@Transactional(readOnly = true)
class AuthService(
    private val jwtProvider: JwtProvider,
    private val tokenCacheRepository: TokenCacheRepository,
    private val userRepository: UserRepository,
    private val userCredentialRepository: UserCredentialRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authCacheRepository: AuthCacheRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository,
) {
    @Transactional
    fun signUp(command: SignUpCommand): User {
        if (userCredentialRepository.existsByEmail(command.email)) {
            throw DuplicateEntityException(kClass = UserCredential::class, name = command.email.value)
        }

        if (userRepository.existsByPhone(command.phone)) {
            throw DuplicateEntityException(kClass = User::class, name = command.phone.value)
        }

        val user = User.create(
            name = command.name,
            phone = command.phone,
            gender = command.gender,
            birthday = command.birthday,
        )

        val userCredential = UserCredential.create(
            user = user,
            email = command.email,
            password = passwordEncoder.encode(command.password),
        )

        val roles = roleRepository.findAllById(command.roleIds)
        val permissions = permissionRepository.findAllById(command.permissionIds)

        roles.forEach { user.addRole(it) }
        permissions.forEach { user.addPermission(it) }

        userRepository.save(user)
        userCredentialRepository.save(userCredential)

        eventPublisher.publishEvent(SendVerifyEmailEvent(email = command.email))

        return user
    }

    @Transactional
    fun signIn(command: SignInCommand): TokenResult {
        val userCredential = userCredentialRepository.findByEmail(command.email)
            ?: throw BadCredentialsException("유효하지 않은 인증 정보입니다.")

        if (!passwordEncoder.matches(command.password, userCredential.password)) {
            throw BadCredentialsException("유효하지 않은 인증 정보입니다.")
        }

        if (!userCredential.isVerified()) {
            throw BadCredentialsException("이메일 인증이 필요합니다.")
        }

        val user = userCredential.user
        val userId = checkNotNull(user.id)

        val accessToken = jwtProvider.createAccessToken(
            userId = userId,
            permissions = getPermissions(user)
        )
        val refreshToken = jwtProvider.createRefreshToken()

        tokenCacheRepository.setRefreshToken(userId = userId, refreshToken = refreshToken)

        return TokenResult(accessToken = accessToken, refreshToken = refreshToken)
    }

    fun refreshToken(command: RefreshTokenCommand): TokenResult {
        val userId = extractUserId(command.accessToken)

        jwtProvider.parseRefreshToken(command.refreshToken)

        val user = userRepository.findById(userId)

        if (tokenCacheRepository.getRefreshToken(userId) != command.refreshToken) {
            throw BadCredentialsException("유효하지 않은 인증 정보입니다.")
        }

        return TokenResult(
            accessToken = jwtProvider.createAccessToken(
                userId = userId,
                permissions = getPermissions(user),
            ),
            refreshToken = command.refreshToken,
        )
    }

    private fun extractUserId(accessToken: String): Long {
        val claims = runCatching { jwtProvider.parseAccessToken(accessToken) }
            .getOrElse { throwable ->
                when (throwable) {
                    is ExpiredJwtException -> throwable.claims
                    else -> throw throwable
                }
            }

        return claims["userId"].toString().toLong()
    }

    private fun getPermissions(user: User): List<String> {
        val rolePermissions = user.userRoles
            .flatMap { it.role.rolePermissions }
            .map { it.permission.name }

        val directPermissions = user.userPermissions
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

        val userCredential = userCredentialRepository.findByEmail(Email.create(email))
            ?: throw BadCredentialsException("유효하지 않은 인증 정보입니다.")

        userCredential.verify()

        authCacheRepository.deleteJoinVerifyToken(command.token)
    }

    fun requestPasswordReset(command: PasswordResetRequestCommand) {
        userCredentialRepository.findByEmail(command.email)
            ?: throw BadCredentialsException("유효하지 않은 인증 정보입니다.")

        eventPublisher.publishEvent(SendPasswordResetEmailEvent(email = command.email))
    }

    @Transactional
    fun confirmPasswordReset(command: PasswordResetConfirmCommand) {
        val email = authCacheRepository.getPasswordResetToken(command.token)
            ?: throw BadCredentialsException("유효하지 않은 인증 정보입니다.")

        val userCredential = userCredentialRepository.findByEmail(Email.create(email))
            ?: throw BadCredentialsException("유효하지 않은 인증 정보입니다.")

        userCredential.updatePassword(passwordEncoder.encode(command.password))

        authCacheRepository.deletePasswordResetToken(command.token)
    }
}