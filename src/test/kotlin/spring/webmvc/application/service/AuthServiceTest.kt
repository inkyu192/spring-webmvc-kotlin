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
import spring.webmvc.application.event.SendPasswordResetEmailEvent
import spring.webmvc.application.event.SendVerifyEmailEvent
import spring.webmvc.domain.dto.command.*
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.enums.MemberStatus
import spring.webmvc.domain.model.enums.MemberType
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.domain.repository.cache.AuthCacheRepository
import spring.webmvc.domain.repository.cache.TokenCacheRepository
import spring.webmvc.infrastructure.security.JwtProvider
import java.time.LocalDate

class AuthServiceTest {
    private val jwtProvider = mockk<JwtProvider>()
    private val tokenCacheRepository = mockk<TokenCacheRepository>()
    private val memberRepository = mockk<MemberRepository>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val authCacheRepository = mockk<AuthCacheRepository>()
    private val eventPublisher = mockk<ApplicationEventPublisher>()
    private val authService = AuthService(
        jwtProvider = jwtProvider,
        tokenCacheRepository = tokenCacheRepository,
        memberRepository = memberRepository,
        passwordEncoder = passwordEncoder,
        authCacheRepository = authCacheRepository,
        eventPublisher = eventPublisher,
    )

    private lateinit var member: Member
    private lateinit var email: Email
    private val memberId = 1L
    private val accessToken = "accessToken"
    private val refreshToken = "refreshToken"

    @BeforeEach
    fun setUp() {
        email = Email.create("test@example.com")
        member = spyk(
            Member.create(
                email = email,
                password = "encodedPassword",
                name = "홍길동",
                phone = Phone.create("010-1234-5678"),
                birthDate = LocalDate.of(1990, 1, 1),
                type = MemberType.CUSTOMER,
            )
        )
        member.updateStatus(MemberStatus.ACTIVE)
        every { member.id } returns memberId
    }

    @Test
    @DisplayName("로그인 성공")
    fun login() {
        val command = LoginCommand(email = email, password = "password123")

        every { memberRepository.findByEmail(email) } returns member
        every { passwordEncoder.matches(command.password, "encodedPassword") } returns true
        every { jwtProvider.createAccessToken(memberId, emptyList()) } returns accessToken
        every { jwtProvider.createRefreshToken() } returns refreshToken
        every { tokenCacheRepository.setRefreshToken(memberId, refreshToken) } just runs

        val result = authService.login(command)

        Assertions.assertThat(result.accessToken).isEqualTo(accessToken)
        Assertions.assertThat(result.refreshToken).isEqualTo(refreshToken)
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 시 BadCredentialsException 발생")
    fun loginWithNonExistentEmail() {
        val command = LoginCommand(email = email, password = "password123")

        every { memberRepository.findByEmail(email) } returns null

        Assertions.assertThatThrownBy { authService.login(command) }
            .isInstanceOf(BadCredentialsException::class.java)
            .hasMessage("유효하지 않은 인증 정보입니다.")
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 BadCredentialsException 발생")
    fun loginWithWrongPassword() {
        val command = LoginCommand(email = email, password = "wrongPassword")

        every { memberRepository.findByEmail(email) } returns member
        every { passwordEncoder.matches(command.password, "encodedPassword") } returns false

        Assertions.assertThatThrownBy { authService.login(command) }
            .isInstanceOf(BadCredentialsException::class.java)
            .hasMessage("유효하지 않은 인증 정보입니다.")
    }

    @Test
    @DisplayName("비활성 계정으로 로그인 시 DisabledException 발생")
    fun loginWithInactiveMember() {
        val command = LoginCommand(email = email, password = "password123")

        every { memberRepository.findByEmail(email) } returns member
        every { passwordEncoder.matches(command.password, "encodedPassword") } returns true
        every { member.isNotActive() } returns true

        Assertions.assertThatThrownBy { authService.login(command) }
            .isInstanceOf(DisabledException::class.java)
            .hasMessage("계정이 활성화되지 않았습니다.")
    }

    @Test
    @DisplayName("토큰 갱신 성공")
    fun refreshToken() {
        val command = RefreshTokenCommand(accessToken = accessToken, refreshToken = refreshToken)
        val claims = DefaultClaims(mapOf("memberId" to memberId))

        every { jwtProvider.parseAccessToken(accessToken) } throws ExpiredJwtException(null, claims, "Token expired")
        every { jwtProvider.parseRefreshToken(refreshToken) } returns mockk()
        every { memberRepository.findById(memberId) } returns member
        every { tokenCacheRepository.getRefreshToken(memberId) } returns refreshToken
        every { jwtProvider.createAccessToken(memberId, emptyList()) } returns "newAccessToken"

        val result = authService.refreshToken(command)

        Assertions.assertThat(result.accessToken).isEqualTo("newAccessToken")
        Assertions.assertThat(result.refreshToken).isEqualTo(refreshToken)
    }

    @Test
    @DisplayName("유효하지 않은 refresh token으로 갱신 시 BadCredentialsException 발생")
    fun refreshTokenWithInvalidToken() {
        val command = RefreshTokenCommand(accessToken = accessToken, refreshToken = "invalidRefreshToken")
        val claims = DefaultClaims(mapOf("memberId" to memberId))

        every { jwtProvider.parseAccessToken(accessToken) } throws ExpiredJwtException(null, claims, "Token expired")
        every { jwtProvider.parseRefreshToken("invalidRefreshToken") } returns mockk()
        every { memberRepository.findById(memberId) } returns member
        every { tokenCacheRepository.getRefreshToken(memberId) } returns refreshToken

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
        member.updateStatus(MemberStatus.PENDING)

        every { authCacheRepository.getJoinVerifyToken(token) } returns email.value
        every { memberRepository.findByEmail(any()) } returns member
        every { authCacheRepository.deleteJoinVerifyToken(token) } just runs

        authService.confirmJoinVerify(command)

        Assertions.assertThat(member.status).isEqualTo(MemberStatus.ACTIVE)
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

        every { memberRepository.findByEmail(email) } returns member
        every { eventPublisher.publishEvent(SendPasswordResetEmailEvent(email)) } just runs

        authService.requestPasswordReset(command)

        verify { memberRepository.findByEmail(email) }
        verify { eventPublisher.publishEvent(SendPasswordResetEmailEvent(email)) }
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 비밀번호 재설정 요청 시 BadCredentialsException 발생")
    fun requestPasswordResetWithNonExistentEmail() {
        val command = PasswordResetRequestCommand(email = email)

        every { memberRepository.findByEmail(email) } returns null

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
        every { memberRepository.findByEmail(any()) } returns member
        every { passwordEncoder.encode(newPassword) } returns encodedPassword
        every { authCacheRepository.deletePasswordResetToken(token) } just runs

        authService.confirmPasswordReset(command)

        Assertions.assertThat(member.password).isEqualTo(encodedPassword)
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
