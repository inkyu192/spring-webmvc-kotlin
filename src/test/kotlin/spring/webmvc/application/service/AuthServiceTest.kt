package spring.webmvc.application.service

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.impl.DefaultClaims
import io.mockk.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import spring.webmvc.application.dto.command.*
import spring.webmvc.application.event.SendPasswordResetEmailEvent
import spring.webmvc.application.event.SendVerifyEmailEvent
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.entity.UserCredential
import spring.webmvc.domain.model.enums.Gender
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
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
import java.time.LocalDate

class AuthServiceTest {
    private val jwtProvider = mockk<JwtProvider>()
    private val tokenCacheRepository = mockk<TokenCacheRepository>()
    private val userRepository = mockk<UserRepository>()
    private val userCredentialRepository = mockk<UserCredentialRepository>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val authCacheRepository = mockk<AuthCacheRepository>()
    private val eventPublisher = mockk<ApplicationEventPublisher>()
    private val roleRepository = mockk<RoleRepository>()
    private val permissionRepository = mockk<PermissionRepository>()
    private val s3Service = mockk<S3Service>()
    private val authService = AuthService(
        jwtProvider = jwtProvider,
        tokenCacheRepository = tokenCacheRepository,
        userRepository = userRepository,
        userCredentialRepository = userCredentialRepository,
        passwordEncoder = passwordEncoder,
        authCacheRepository = authCacheRepository,
        eventPublisher = eventPublisher,
        roleRepository = roleRepository,
        permissionRepository = permissionRepository,
        s3Service = s3Service,
    )

    private lateinit var user: User
    private lateinit var userCredential: UserCredential
    private lateinit var email: Email
    private val userId = 1L
    private val accessToken = "accessToken"
    private val refreshToken = "refreshToken"

    @BeforeEach
    fun setUp() {
        email = Email.create("test@example.com")
        user = spyk(
            User.create(
                name = "홍길동",
                phone = Phone.create("010-1234-5678"),
                gender = Gender.MALE,
                birthday = LocalDate.of(1990, 1, 1),
            )
        )
        every { user.id } returns userId

        userCredential = spyk(
            UserCredential.create(
                user = user,
                email = email,
                password = "encodedPassword",
            )
        )
        userCredential.verify()
    }

    @Test
    @DisplayName("회원가입 성공")
    fun signUp() {
        val profileImageKey = "temp/test-image.jpg"
        val command = SignUpCommand(
            email = email,
            password = "password123",
            name = "홍길동",
            phone = Phone.create("010-1234-5678"),
            gender = Gender.MALE,
            birthday = LocalDate.of(1990, 1, 1),
            profileImageKey = profileImageKey,
            roleIds = emptyList(),
            permissionIds = emptyList(),
        )

        mockkObject(User.Companion)
        every {
            User.create(
                name = any(),
                phone = any(),
                gender = any(),
                birthday = any(),
            )
        } returns user
        every { userCredentialRepository.existsByEmail(email) } returns false
        every { userRepository.existsByPhone(any()) } returns false
        every { roleRepository.findAllById(emptyList()) } returns emptyList()
        every { permissionRepository.findAllById(emptyList()) } returns emptyList()
        every { passwordEncoder.encode(any()) } returns "encodedPassword"
        every { userRepository.save(any()) } returns user
        every {
            s3Service.copyObject(
                profileImageKey,
                FileType.PROFILE,
                userId
            )
        } returns "data/profile/$userId/test-image.jpg"
        every { userCredentialRepository.save(any()) } returns userCredential
        every { eventPublisher.publishEvent(SendVerifyEmailEvent(email)) } just runs

        val result = authService.signUp(command)

        Assertions.assertThat(result).isNotNull
        verify { userCredentialRepository.existsByEmail(email) }
        verify { userRepository.existsByPhone(any()) }
        verify { userRepository.save(any()) }
        verify { s3Service.copyObject(profileImageKey, FileType.PROFILE, userId) }
        verify { userCredentialRepository.save(any()) }
        verify { eventPublisher.publishEvent(SendVerifyEmailEvent(email)) }

        unmockkObject(User.Companion)
    }

    @Test
    @DisplayName("프로필 이미지 없이 회원가입 성공")
    fun signUpWithoutProfileImage() {
        val command = SignUpCommand(
            email = email,
            password = "password123",
            name = "홍길동",
            phone = Phone.create("010-1234-5678"),
            gender = Gender.MALE,
            birthday = LocalDate.of(1990, 1, 1),
            profileImageKey = null,
            roleIds = emptyList(),
            permissionIds = emptyList(),
        )

        every { userCredentialRepository.existsByEmail(email) } returns false
        every { userRepository.existsByPhone(any()) } returns false
        every { roleRepository.findAllById(emptyList()) } returns emptyList()
        every { permissionRepository.findAllById(emptyList()) } returns emptyList()
        every { passwordEncoder.encode(any()) } returns "encodedPassword"
        every { userRepository.save(any()) } returns user
        every { userCredentialRepository.save(any()) } returns userCredential
        every { eventPublisher.publishEvent(SendVerifyEmailEvent(email)) } just runs

        val result = authService.signUp(command)

        Assertions.assertThat(result).isNotNull
        verify(exactly = 0) { s3Service.copyObject(any(), any(), any()) }
    }

    @Test
    @DisplayName("중복된 이메일로 회원가입 시 DuplicateEntityException 발생")
    fun signUpWithDuplicateEmail() {
        val command = SignUpCommand(
            email = email,
            password = "password123",
            name = "홍길동",
            phone = Phone.create("010-1234-5678"),
            gender = Gender.MALE,
            birthday = LocalDate.of(1990, 1, 1),
            profileImageKey = null,
            roleIds = emptyList(),
            permissionIds = emptyList(),
        )

        every { userCredentialRepository.existsByEmail(email) } returns true

        Assertions.assertThatThrownBy { authService.signUp(command) }
            .isInstanceOf(DuplicateEntityException::class.java)
    }

    @Test
    @DisplayName("중복된 번호로 회원가입 시 DuplicateEntityException 발생")
    fun signUpWithDuplicatePhone() {
        val phone = Phone.create("010-1234-5678")
        val command = SignUpCommand(
            email = email,
            password = "password123",
            name = "홍길동",
            phone = phone,
            gender = Gender.MALE,
            birthday = LocalDate.of(1990, 1, 1),
            profileImageKey = null,
            roleIds = emptyList(),
            permissionIds = emptyList(),
        )

        every { userCredentialRepository.existsByEmail(email) } returns false
        every { userRepository.existsByPhone(phone) } returns true

        Assertions.assertThatThrownBy { authService.signUp(command) }
            .isInstanceOf(DuplicateEntityException::class.java)
    }

    @Test
    @DisplayName("로그인 성공")
    fun signIn() {
        val command = SignInCommand(email = email, password = "password123")

        every { userCredentialRepository.findByEmail(email) } returns userCredential
        every { passwordEncoder.matches(command.password, "encodedPassword") } returns true
        every { jwtProvider.createAccessToken(userId, emptyList()) } returns accessToken
        every { jwtProvider.createRefreshToken() } returns refreshToken
        every { tokenCacheRepository.addRefreshToken(userId, refreshToken) } just runs

        val result = authService.signIn(command)

        Assertions.assertThat(result.accessToken).isEqualTo(accessToken)
        Assertions.assertThat(result.refreshToken).isEqualTo(refreshToken)
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 시 NotFoundEntityException 발생")
    fun signInWithNonExistentEmail() {
        val command = SignInCommand(email = email, password = "password123")

        every { userCredentialRepository.findByEmail(email) } returns null

        Assertions.assertThatThrownBy { authService.signIn(command) }
            .isInstanceOf(NotFoundEntityException::class.java)
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 BadCredentialsException 발생")
    fun signInWithWrongPassword() {
        val command = SignInCommand(email = email, password = "wrongPassword")

        every { userCredentialRepository.findByEmail(email) } returns userCredential
        every { passwordEncoder.matches(command.password, "encodedPassword") } returns false

        Assertions.assertThatThrownBy { authService.signIn(command) }
            .isInstanceOf(BadCredentialsException::class.java)
    }

    @Test
    @DisplayName("이메일 인증 안된 계정으로 로그인 시 BadCredentialsException 발생")
    fun signInWithUnverifiedEmail() {
        val command = SignInCommand(email = email, password = "password123")
        val unverifiedCredential = spyk(
            UserCredential.create(
                user = user,
                email = email,
                password = "encodedPassword",
            )
        )

        every { userCredentialRepository.findByEmail(email) } returns unverifiedCredential
        every { passwordEncoder.matches(command.password, "encodedPassword") } returns true

        Assertions.assertThatThrownBy { authService.signIn(command) }
            .isInstanceOf(BadCredentialsException::class.java)
    }

    @Test
    @DisplayName("토큰 갱신 성공")
    fun refreshToken() {
        val command = RefreshTokenCommand(accessToken = accessToken, refreshToken = refreshToken)
        val claims = DefaultClaims(mapOf("userId" to userId))
        val newRefreshToken = "newRefreshToken"

        every { jwtProvider.parseAccessToken(accessToken) } throws ExpiredJwtException(null, claims, "Token expired")
        every { jwtProvider.parseRefreshToken(refreshToken) } returns mockk()
        every { userRepository.findById(userId) } returns user
        every { tokenCacheRepository.getRefreshToken(userId, refreshToken) } returns refreshToken
        every { tokenCacheRepository.removeRefreshToken(userId, refreshToken) } just runs
        every { jwtProvider.createRefreshToken() } returns newRefreshToken
        every { tokenCacheRepository.addRefreshToken(userId, newRefreshToken) } just runs
        every { jwtProvider.createAccessToken(userId, emptyList()) } returns "newAccessToken"

        val result = authService.refreshToken(command)

        Assertions.assertThat(result.accessToken).isEqualTo("newAccessToken")
        Assertions.assertThat(result.refreshToken).isEqualTo(newRefreshToken)
        verify { tokenCacheRepository.removeRefreshToken(userId, refreshToken) }
        verify { tokenCacheRepository.addRefreshToken(userId, newRefreshToken) }
    }

    @Test
    @DisplayName("유효하지 않은 refresh token으로 갱신 시 BadCredentialsException 발생")
    fun refreshTokenWithInvalidToken() {
        val command = RefreshTokenCommand(accessToken = accessToken, refreshToken = "invalidRefreshToken")
        val claims = DefaultClaims(mapOf("userId" to userId))

        every { jwtProvider.parseAccessToken(accessToken) } throws ExpiredJwtException(null, claims, "Token expired")
        every { jwtProvider.parseRefreshToken("invalidRefreshToken") } returns mockk()
        every { userRepository.findById(userId) } returns user
        every { tokenCacheRepository.getRefreshToken(userId, "invalidRefreshToken") } returns null

        Assertions.assertThatThrownBy { authService.refreshToken(command) }
            .isInstanceOf(BadCredentialsException::class.java)
    }

    @Test
    @DisplayName("회원가입 인증 요청")
    fun requestJoinVerify() {
        val command = JoinVerifyRequestCommand(email = email)

        every { eventPublisher.publishEvent(SendVerifyEmailEvent(email)) } just runs

        authService.requestJoinVerify(command)

        verify { eventPublisher.publishEvent(SendVerifyEmailEvent(email)) }
    }

    @Test
    @DisplayName("회원가입 인증 확인 성공")
    fun confirmJoinVerify() {
        val token = "verifyToken"
        val command = JoinVerifyConfirmCommand(token = token)
        val unverifiedCredential = spyk(
            UserCredential.create(
                user = user,
                email = email,
                password = "encodedPassword",
            )
        )

        every { authCacheRepository.getJoinVerifyToken(token) } returns email.value
        every { userCredentialRepository.findByEmail(any()) } returns unverifiedCredential
        every { authCacheRepository.deleteJoinVerifyToken(token) } just runs

        authService.confirmJoinVerify(command)

        Assertions.assertThat(unverifiedCredential.isVerified()).isTrue
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 회원가입 인증 확인 시 BadCredentialsException 발생")
    fun confirmJoinVerifyWithInvalidToken() {
        val token = "invalidToken"
        val command = JoinVerifyConfirmCommand(token = token)

        every { authCacheRepository.getJoinVerifyToken(token) } returns null

        Assertions.assertThatThrownBy { authService.confirmJoinVerify(command) }
            .isInstanceOf(BadCredentialsException::class.java)
    }

    @Test
    @DisplayName("비밀번호 재설정 요청")
    fun requestPasswordReset() {
        val command = PasswordResetRequestCommand(email = email)

        every { userCredentialRepository.findByEmail(email) } returns userCredential
        every { eventPublisher.publishEvent(SendPasswordResetEmailEvent(email)) } just runs

        authService.requestPasswordReset(command)

        verify { userCredentialRepository.findByEmail(email) }
        verify { eventPublisher.publishEvent(SendPasswordResetEmailEvent(email)) }
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 비밀번호 재설정 요청 시 NotFoundEntityException 발생")
    fun requestPasswordResetWithNonExistentEmail() {
        val command = PasswordResetRequestCommand(email = email)

        every { userCredentialRepository.findByEmail(email) } returns null

        Assertions.assertThatThrownBy { authService.requestPasswordReset(command) }
            .isInstanceOf(NotFoundEntityException::class.java)
    }

    @Test
    @DisplayName("비밀번호 재설정 확인 성공")
    fun confirmPasswordReset() {
        val token = "resetToken"
        val newPassword = "newPassword123"
        val encodedPassword = "encodedNewPassword"
        val command = PasswordResetConfirmCommand(token = token, password = newPassword)

        every { authCacheRepository.getPasswordResetToken(token) } returns email.value
        every { userCredentialRepository.findByEmail(any()) } returns userCredential
        every { passwordEncoder.encode(newPassword) } returns encodedPassword
        every { authCacheRepository.deletePasswordResetToken(token) } just runs

        authService.confirmPasswordReset(command)

        Assertions.assertThat(userCredential.password).isEqualTo(encodedPassword)
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 비밀번호 재설정 확인 시 BadCredentialsException 발생")
    fun confirmPasswordResetWithInvalidToken() {
        val token = "invalidToken"
        val command = PasswordResetConfirmCommand(token = token, password = "newPassword123")

        every { authCacheRepository.getPasswordResetToken(token) } returns null

        Assertions.assertThatThrownBy { authService.confirmPasswordReset(command) }
            .isInstanceOf(BadCredentialsException::class.java)
    }
}
