package spring.webmvc.application.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.domain.repository.TokenRepository
import spring.webmvc.infrastructure.config.security.JwtProvider
import spring.webmvc.presentation.dto.request.MemberLoginRequest
import spring.webmvc.presentation.dto.request.TokenRequest

class AuthServiceTest : DescribeSpec({
    val jwtProvider = mockk<JwtProvider>()
    val tokenRepository = mockk<TokenRepository>()
    val memberRepository = mockk<MemberRepository>()
    val passwordEncoder = mockk<PasswordEncoder>()
    val authService = AuthService(
        jwtProvider = jwtProvider,
        tokenRepository = tokenRepository,
        memberRepository = memberRepository,
        passwordEncoder = passwordEncoder
    )

    describe("login 은") {
        val request = MemberLoginRequest(account = "account", password = "password")

        context("계정이 존재하지 않을 경우") {
            it("BadCredentialsException 던진다") {
                every { memberRepository.findByAccount(request.account) } returns null

                shouldThrow<BadCredentialsException> { authService.login(request) }
            }
        }

        context("비밀번호가 일치하지 않을 경우") {
            it("BadCredentialsException 던진다") {
                val member = mockk<Member>(relaxed = true)

                every { memberRepository.findByAccount(request.account) } returns member
                every { passwordEncoder.matches(request.password, member.password) } returns false

                shouldThrow<BadCredentialsException> { authService.login(request) }
            }
        }

        context("비밀번호가 일치할 경우") {
            it("토큰을 발급한다") {
                val accessToken = "accessToken"
                val refreshToken = "refreshToken"

                val member = mockk<Member>(relaxed = true)

                every { memberRepository.findByAccount(any()) } returns member
                every { passwordEncoder.matches(any(), any()) } returns true
                every { jwtProvider.createAccessToken(memberId = any(), permissions = any()) } returns accessToken
                every { jwtProvider.createRefreshToken() } returns refreshToken
                every { tokenRepository.save(memberId = any(), token = any()) } returns refreshToken

                val response = authService.login(request)

                verify(exactly = 1) { tokenRepository.save(memberId = any(), token = any()) }
                response.accessToken shouldBe accessToken
                response.refreshToken shouldBe refreshToken
            }
        }
    }

    describe("refreshToken 은") {
        val request = TokenRequest("oldAccessToken", "refreshToken")

        context("Access 토큰이 유효하지 않을 경우") {
            it("JwtException 던진다") {
                every { jwtProvider.parseAccessToken(request.accessToken) } throws JwtException("invalid access token")

                shouldThrow<JwtException> { authService.refreshToken(request) }
            }
        }

        context("Refresh 토큰이 유효하지 않을 경우") {
            it("JwtException 던진다") {
                val memberId = 1L
                val claims = mockk<Claims>()

                every { jwtProvider.parseAccessToken(request.accessToken) } returns claims
                every { jwtProvider.parseRefreshToken(request.refreshToken) } throws JwtException("invalid refresh token")
                every { claims["memberId"] } returns memberId

                shouldThrow<JwtException> { authService.refreshToken(request) }
            }
        }

        context("Refresh 토큰이 일치하지 않을 경우") {
            it("BadCredentialsException 던진다") {
                val memberId = 1L
                val claims = mockk<Claims>()
                val token = "realRefreshToken"
                val member = mockk<Member>()

                every { jwtProvider.parseAccessToken(request.accessToken) } returns claims
                every { jwtProvider.parseRefreshToken(request.refreshToken) } returns mockk()
                every { claims["memberId"] } returns memberId
                every { tokenRepository.findByMemberIdOrNull(memberId) } returns token
                every { memberRepository.findByIdOrNull(memberId) } returns member

                shouldThrow<BadCredentialsException> { authService.refreshToken(request) }
            }
        }

        context("토큰이 유효할 경우") {
            it("Access 토큰이 갱신된다") {
                val memberId = 1L
                val claims = mockk<Claims>()
                val token = "refreshToken"
                val member = mockk<Member>(relaxed = true)

                every { jwtProvider.parseAccessToken(request.accessToken) } returns claims
                every { jwtProvider.parseRefreshToken(request.refreshToken) } returns mockk()
                every { claims["memberId"] } returns memberId
                every { tokenRepository.findByMemberIdOrNull(memberId) } returns token
                every { memberRepository.findByIdOrNull(memberId) } returns member
                every {
                    jwtProvider.createAccessToken(
                        memberId = any(),
                        permissions = any()
                    )
                } returns "newAccessToken"

                val response = authService.refreshToken(request)

                response.accessToken shouldNotBe request.accessToken
                response.refreshToken shouldBe request.refreshToken
            }
        }
    }
})
