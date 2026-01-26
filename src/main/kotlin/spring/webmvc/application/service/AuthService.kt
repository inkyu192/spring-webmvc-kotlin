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
import spring.webmvc.infrastructure.exception.NotFoundEntityException
import spring.webmvc.infrastructure.external.s3.FileType
import spring.webmvc.infrastructure.external.s3.S3Service
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
    private val s3Service: S3Service,
) {
    @Transactional
    fun signUp(command: SignUpCommand): User {
        if (userCredentialRepository.existsByEmail(command.email)) {
            throw DuplicateEntityException(kClass = UserCredential::class, name = command.email.value)
        }

        if (userRepository.existsByPhone(command.phone)) {
            throw DuplicateEntityException(kClass = User::class, name = command.phone.value)
        }

        var user = User.create(
            name = command.name,
            phone = command.phone,
            gender = command.gender,
            birthday = command.birthday,
        )

        roleRepository.findAllById(command.roleIds).forEach { user.addUserRole(it) }
        permissionRepository.findAllById(command.permissionIds).forEach { user.addUserPermission(it) }

        user = userRepository.save(user)

        command.profileImageKey?.let { tempKey ->
            val profileImage = s3Service.copyObject(
                sourceKey = tempKey,
                fileType = FileType.PROFILE,
                id = checkNotNull(user.id),
            )
            user.updateProfileImage(profileImage)
        }

        val userCredential = UserCredential.create(
            user = user,
            email = command.email,
            password = passwordEncoder.encode(command.password),
        )

        userCredentialRepository.save(userCredential)

        SendVerifyEmailEvent(email = command.email).let { eventPublisher.publishEvent(it) }

        return user
    }

    @Transactional
    fun signIn(command: SignInCommand): TokenResult {
        val userCredential = userCredentialRepository.findByEmail(command.email)
            ?: throw NotFoundEntityException(kClass = UserCredential::class, id = command.email.value)

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
            permissions = user.getPermissionNames()
        )
        val refreshToken = jwtProvider.createRefreshToken()

        tokenCacheRepository.addRefreshToken(userId = userId, refreshToken = refreshToken)

        return TokenResult(accessToken = accessToken, refreshToken = refreshToken)
    }

    fun refreshToken(command: RefreshTokenCommand): TokenResult {
        jwtProvider.parseRefreshToken(command.refreshToken)

        val userId = extractUserId(command.accessToken)

        tokenCacheRepository.getRefreshToken(userId = userId, refreshToken = command.refreshToken)
            ?: throw BadCredentialsException("유효하지 않은 인증 정보입니다.")

        val user = userRepository.findById(userId)

        tokenCacheRepository.removeRefreshToken(userId = userId, refreshToken = command.refreshToken)

        val refreshToken = jwtProvider.createRefreshToken()
        tokenCacheRepository.addRefreshToken(userId = userId, refreshToken = refreshToken)

        return TokenResult(
            accessToken = jwtProvider.createAccessToken(
                userId = userId,
                permissions = user.getPermissionNames(),
            ),
            refreshToken = refreshToken,
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

    fun requestJoinVerify(command: JoinVerifyRequestCommand) {
        SendVerifyEmailEvent(email = command.email).let { eventPublisher.publishEvent(it) }
    }

    @Transactional
    fun confirmJoinVerify(command: JoinVerifyConfirmCommand) {
        val email = authCacheRepository.getJoinVerifyToken(command.token)
            ?: throw BadCredentialsException("유효하지 않은 인증 정보입니다.")

        val userCredential = userCredentialRepository.findByEmail(Email.create(email))
            ?: throw NotFoundEntityException(kClass = UserCredential::class, id = email)

        userCredential.verify()

        authCacheRepository.deleteJoinVerifyToken(command.token)
    }

    fun requestPasswordReset(command: PasswordResetRequestCommand) {
        userCredentialRepository.findByEmail(command.email)
            ?: throw NotFoundEntityException(kClass = UserCredential::class, id = command.email.value)

        SendPasswordResetEmailEvent(email = command.email).let { eventPublisher.publishEvent(it) }
    }

    @Transactional
    fun confirmPasswordReset(command: PasswordResetConfirmCommand) {
        val email = authCacheRepository.getPasswordResetToken(command.token)
            ?: throw BadCredentialsException("유효하지 않은 인증 정보입니다.")

        val userCredential = userCredentialRepository.findByEmail(Email.create(email))
            ?: throw NotFoundEntityException(kClass = UserCredential::class, id = email)

        userCredential.updatePassword(passwordEncoder.encode(command.password))

        authCacheRepository.deletePasswordResetToken(command.token)
    }
}
