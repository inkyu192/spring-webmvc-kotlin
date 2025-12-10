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
import org.springframework.security.authentication.DisabledException
import org.springframework.security.crypto.password.PasswordEncoder
import spring.webmvc.application.dto.command.JoinVerifyConfirmCommand
import spring.webmvc.application.dto.command.JoinVerifyRequestCommand
import spring.webmvc.application.dto.command.PasswordResetConfirmCommand
import spring.webmvc.application.dto.command.PasswordResetRequestCommand
import spring.webmvc.application.dto.command.RefreshTokenCommand
import spring.webmvc.application.dto.command.SignInCommand
import spring.webmvc.application.dto.command.SignUpCommand
import spring.webmvc.application.event.SendPasswordResetEmailEvent
import spring.webmvc.application.event.SendVerifyEmailEvent
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.enums.UserStatus
import spring.webmvc.domain.model.enums.UserType
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import spring.webmvc.domain.repository.PermissionRepository
import spring.webmvc.domain.repository.RoleRepository
import spring.webmvc.domain.repository.UserRepository
import spring.webmvc.domain.repository.cache.AuthCacheRepository
import spring.webmvc.domain.repository.cache.TokenCacheRepository
import spring.webmvc.infrastructure.security.JwtProvider
import spring.webmvc.presentation.exception.DuplicateEntityException
import java.time.LocalDate

class AuthServiceTest {
    private val jwtProvider = mockk<JwtProvider>()
    private val tokenCacheRepository = mockk<TokenCacheRepository>()
    private val userRepository = mockk<UserRepository>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val authCacheRepository = mockk<AuthCacheRepository>()
    private val eventPublisher = mockk<ApplicationEventPublisher>()
    private val roleRepository = mockk<RoleRepository>()
    private val permissionRepository = mockk<PermissionRepository>()
    private val authService = AuthService(
        jwtProvider = jwtProvider,
        tokenCacheRepository = tokenCacheRepository,
        userRepository = userRepository,
        passwordEncoder = passwordEncoder,
        authCacheRepository = authCacheRepository,
        eventPublisher = eventPublisher,
        roleRepository = roleRepository,
        permissionRepository = permissionRepository,
    )

    private lateinit var user: User
    private lateinit var email: Email
    private val userId = 1L
    private val accessToken = "accessToken"
    private val refreshToken = "refreshToken"

    @BeforeEach
    fun setUp() {
        email = Email.create("test@example.com")
        user = spyk(
            User.create(
                email = email,
                password = "encodedPassword",
                name = "홍길동",
                phone = Phone.create("010-1234-5678"),
                birthDate = LocalDate.of(1990, 1, 1),
                type = UserType.CUSTOMER,
            )
        )
        user.updateStatus(UserStatus.ACTIVE)
        every { user.id } returns userId
    }

    @Test
    @DisplayName("회원가입 성공")
    fun signUp() {
        val command = SignUpCommand(
            email = email,
            password = "password123",
            name = "홍길동",
            phone = Phone.create("010-1234-5678"),
            birthDate = LocalDate.of(1990, 1, 1),
            type = UserType.CUSTOMER,
            roleIds = emptyList(),
            permissionIds = emptyList(),
        )

        every { userRepository.existsByEmail(email) } returns false
        every { roleRepository.findAllById(emptyList()) } returns emptyList()
        every { permissionRepository.findAllById(emptyList()) } returns emptyList()
        every { userRepository.save(any()) } returns user
        every { eventPublisher.publishEvent(SendVerifyEmailEvent(email)) } just runs

        val result = authService.signUp(command)

        Assertions.assertThat(result).isNotNull
        verify { userRepository.save(any()) }
        verify { eventPublisher.publishEvent(SendVerifyEmailEvent(email)) }
    }

    @Test
    @DisplayName("중복된 이메일로 회원가입 시 DuplicateEntityException 발생")
    fun signUpWithDuplicateEmail() {
        val command = SignUpCommand(
            email = email,
            password = "password123",
            name = "홍길동",
            phone = Phone.create("010-1234-5678"),
            birthDate = LocalDate.of(1990, 1, 1),
            type = UserType.CUSTOMER,
            roleIds = emptyList(),
            permissionIds = emptyList(),
        )

        every { userRepository.existsByEmail(email) } returns true

        Assertions.assertThatThrownBy { authService.signUp(command) }
            .isInstanceOf(DuplicateEntityException::class.java)
    }

    @Test
    @DisplayName("로그인 성공")
    fun signIn() {
        val command = SignInCommand(email = email, password = "password123")

        every { userRepository.findByEmail(email) } returns user
        every { passwordEncoder.matches(command.password, "encodedPassword") } returns true
        every { jwtProvider.createAccessToken(userId, emptyList()) } returns accessToken
        every { jwtProvider.createRefreshToken() } returns refreshToken
        every { tokenCacheRepository.setRefreshToken(userId, refreshToken) } just runs

        val result = authService.signIn(command)

        Assertions.assertThat(result.accessToken).isEqualTo(accessToken)
        Assertions.assertThat(result.refreshToken).isEqualTo(refreshToken)
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 시 BadCredentialsException 발생")
    fun signInWithNonExistentEmail() {
        val command = SignInCommand(email = email, password = "password123")

        every { userRepository.findByEmail(email) } returns null

        Assertions.assertThatThrownBy { authService.signIn(command) }
            .isInstanceOf(BadCredentialsException::class.java)
            .hasMessage("유효하지 않은 인증 정보입니다.")
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 BadCredentialsException 발생")
    fun signInWithWrongPassword() {
        val command = SignInCommand(email = email, password = "wrongPassword")

        every { userRepository.findByEmail(email) } returns user
        every { passwordEncoder.matches(command.password, "encodedPassword") } returns false

        Assertions.assertThatThrownBy { authService.signIn(command) }
            .isInstanceOf(BadCredentialsException::class.java)
            .hasMessage("유효하지 않은 인증 정보입니다.")
    }

    @Test
    @DisplayName("비활성 계정으로 로그인 시 DisabledException 발생")
    fun signInWithInactiveUser() {
        val command = SignInCommand(email = email, password = "password123")

        every { userRepository.findByEmail(email) } returns user
        every { passwordEncoder.matches(command.password, "encodedPassword") } returns true
        every { user.isNotActive() } returns true

        Assertions.assertThatThrownBy { authService.signIn(command) }
            .isInstanceOf(DisabledException::class.java)
            .hasMessage("계정이 활성화되지 않았습니다.")
    }

    @Test
    @DisplayName("토큰 갱신 성공")
    fun refreshToken() {
        val command = RefreshTokenCommand(accessToken = accessToken, refreshToken = refreshToken)
        val claims = DefaultClaims(mapOf("userId" to userId))

        every { jwtProvider.parseAccessToken(accessToken) } throws ExpiredJwtException(null, claims, "Token expired")
        every { jwtProvider.parseRefreshToken(refreshToken) } returns mockk()
        every { userRepository.findById(userId) } returns user
        every { tokenCacheRepository.getRefreshToken(userId) } returns refreshToken
        every { jwtProvider.createAccessToken(userId, emptyList()) } returns "newAccessToken"

        val result = authService.refreshToken(command)

        Assertions.assertThat(result.accessToken).isEqualTo("newAccessToken")
        Assertions.assertThat(result.refreshToken).isEqualTo(refreshToken)
    }

    @Test
    @DisplayName("유효하지 않은 refresh token으로 갱신 시 BadCredentialsException 발생")
    fun refreshTokenWithInvalidToken() {
        val command = RefreshTokenCommand(accessToken = accessToken, refreshToken = "invalidRefreshToken")
        val claims = DefaultClaims(mapOf("userId" to userId))

        every { jwtProvider.parseAccessToken(accessToken) } throws ExpiredJwtException(null, claims, "Token expired")
        every { jwtProvider.parseRefreshToken("invalidRefreshToken") } returns mockk()
        every { userRepository.findById(userId) } returns user
        every { tokenCacheRepository.getRefreshToken(userId) } returns refreshToken

        Assertions.assertThatThrownBy { authService.refreshToken(command) }
            .isInstanceOf(BadCredentialsException::class.java)
            .hasMessage("유효하지 않은 인증 정보입니다.")
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
        user.updateStatus(UserStatus.PENDING)

        every { authCacheRepository.getJoinVerifyToken(token) } returns email.value
        every { userRepository.findByEmail(any()) } returns user
        every { authCacheRepository.deleteJoinVerifyToken(token) } just runs

        authService.confirmJoinVerify(command)

        Assertions.assertThat(user.status).isEqualTo(UserStatus.ACTIVE)
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 회원가입 인증 확인 시 BadCredentialsException 발생")
    fun confirmJoinVerifyWithInvalidToken() {
        val token = "invalidToken"
        val command = JoinVerifyConfirmCommand(token = token)

        every { authCacheRepository.getJoinVerifyToken(token) } returns null

        Assertions.assertThatThrownBy { authService.confirmJoinVerify(command) }
            .isInstanceOf(BadCredentialsException::class.java)
            .hasMessage("유효하지 않은 인증 정보입니다.")
    }

    @Test
    @DisplayName("비밀번호 재설정 요청")
    fun requestPasswordReset() {
        val command = PasswordResetRequestCommand(email = email)

        every { userRepository.findByEmail(email) } returns user
        every { eventPublisher.publishEvent(SendPasswordResetEmailEvent(email)) } just runs

        authService.requestPasswordReset(command)

        verify { userRepository.findByEmail(email) }
        verify { eventPublisher.publishEvent(SendPasswordResetEmailEvent(email)) }
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 비밀번호 재설정 요청 시 BadCredentialsException 발생")
    fun requestPasswordResetWithNonExistentEmail() {
        val command = PasswordResetRequestCommand(email = email)

        every { userRepository.findByEmail(email) } returns null

        Assertions.assertThatThrownBy { authService.requestPasswordReset(command) }
            .isInstanceOf(BadCredentialsException::class.java)
            .hasMessage("유효하지 않은 인증 정보입니다.")
    }

    @Test
    @DisplayName("비밀번호 재설정 확인 성공")
    fun confirmPasswordReset() {
        val token = "resetToken"
        val newPassword = "newPassword123"
        val encodedPassword = "encodedNewPassword"
        val command = PasswordResetConfirmCommand(token = token, password = newPassword)

        every { authCacheRepository.getPasswordResetToken(token) } returns email.value
        every { userRepository.findByEmail(any()) } returns user
        every { passwordEncoder.encode(newPassword) } returns encodedPassword
        every { authCacheRepository.deletePasswordResetToken(token) } just runs

        authService.confirmPasswordReset(command)

        Assertions.assertThat(user.password).isEqualTo(encodedPassword)
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 비밀번호 재설정 확인 시 BadCredentialsException 발생")
    fun confirmPasswordResetWithInvalidToken() {
        val token = "invalidToken"
        val command = PasswordResetConfirmCommand(token = token, password = "newPassword123")

        every { authCacheRepository.getPasswordResetToken(token) } returns null

        Assertions.assertThatThrownBy { authService.confirmPasswordReset(command) }
            .isInstanceOf(BadCredentialsException::class.java)
            .hasMessage("유효하지 않은 인증 정보입니다.")
    }
}
