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
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.domain.repository.cache.TokenCacheRepository
import spring.webmvc.infrastructure.security.JwtProvider

class AuthServiceTest : DescribeSpec({
    val jwtProvider = mockk<JwtProvider>()
    val tokenCacheRepository = mockk<TokenCacheRepository>()
    val memberRepository = mockk<MemberRepository>()
    val passwordEncoder = mockk<PasswordEncoder>()
    val authService = AuthService(
        jwtProvider = jwtProvider,
        tokenCacheRepository = tokenCacheRepository,
        memberRepository = memberRepository,
        passwordEncoder = passwordEncoder
    )

    lateinit var email: String
    lateinit var password: String
    lateinit var member: Member
    lateinit var accessToken: String
    lateinit var refreshToken: String
    lateinit var newAccessToken: String
    lateinit var fakeRefreshToken: String
    lateinit var claims: Claims
    val memberId = 1L

    beforeEach {
        email = "test@gmail.com"
        password = "password"
        member = mockk<Member>(relaxed = true)
        accessToken = "accessToken"
        refreshToken = "refreshToken"
        newAccessToken = "newAccessToken"
        fakeRefreshToken = "fakeRefreshToken"
        claims = mockk<Claims>()
    }

    describe("login") {
        context("Member 엔티티 없을 경우") {
            it("BadCredentialsException 발생한다") {
                every { memberRepository.findByEmail(email = any<Email>()) } returns null

                shouldThrow<BadCredentialsException> { authService.login(email = email, password = password) }
            }
        }

        context("비밀번호가 일치하지 않을 경우") {
            it("BadCredentialsException 발생한다") {
                every { memberRepository.findByEmail(email = any<Email>()) } returns member
                every { passwordEncoder.matches(password, member.password) } returns false

                shouldThrow<BadCredentialsException> { authService.login(email = email, password = password) }
            }
        }

        context("유효성 검사 성공할 경우") {
            it("Token 저장 후 반환한다") {
                every { memberRepository.findByEmail(email = any<Email>()) } returns member
                every { passwordEncoder.matches(any(), any()) } returns true
                every { jwtProvider.createAccessToken(memberId = any(), permissions = any()) } returns accessToken
                every { jwtProvider.createRefreshToken() } returns refreshToken
                every { tokenCacheRepository.setRefreshToken(memberId = any(), refreshToken = any<String>()) } returns Unit

                val result = authService.login(email = email, password = password)

                verify(exactly = 1) { tokenCacheRepository.setRefreshToken(memberId = any(), refreshToken = any<String>()) }
                result.accessToken shouldBe accessToken
                result.refreshToken shouldBe refreshToken
            }
        }
    }

    describe("refreshToken") {
        context("accessToken 유효하지 않을 경우") {
            it("JwtException 발생한다") {
                every { jwtProvider.parseAccessToken(accessToken) } throws JwtException("invalid access token")

                shouldThrow<JwtException> {
                    authService.refreshToken(accessToken = accessToken, refreshToken = refreshToken)
                }
            }
        }

        context("refreshToken 유효하지 않을 경우") {
            it("JwtException 발생한다") {
                every { jwtProvider.parseAccessToken(accessToken) } returns claims
                every { jwtProvider.parseRefreshToken(refreshToken) } throws JwtException("invalid refresh token")
                every { claims["memberId"] } returns memberId

                shouldThrow<JwtException> {
                    authService.refreshToken(accessToken = accessToken, refreshToken = refreshToken)
                }
            }
        }

        context("refreshToken 일치하지 않을 경우") {
            it("BadCredentialsException 발생한다") {
                every { jwtProvider.parseAccessToken(accessToken) } returns claims
                every { memberRepository.findById(memberId) } returns member
                every { claims["memberId"] } returns memberId
                every { jwtProvider.parseRefreshToken(fakeRefreshToken) } returns claims
                every { tokenCacheRepository.getRefreshToken(memberId) } returns refreshToken

                shouldThrow<BadCredentialsException> {
                    authService.refreshToken(accessToken = accessToken, refreshToken = fakeRefreshToken)
                }
            }
        }

        context("유효성 검사 성공할 경우") {
            it("AccessToken 갱신된다") {
                every { jwtProvider.parseAccessToken(accessToken) } returns claims
                every { memberRepository.findById(memberId) } returns member
                every { claims["memberId"] } returns memberId
                every { jwtProvider.parseRefreshToken(refreshToken) } returns claims
                every { tokenCacheRepository.getRefreshToken(memberId) } returns refreshToken
                every {
                    jwtProvider.createAccessToken(
                        memberId = any(),
                        permissions = any()
                    )
                } returns newAccessToken

                val result = authService.refreshToken(accessToken = accessToken, refreshToken = refreshToken)

                result.accessToken shouldNotBe accessToken
                result.refreshToken shouldBe refreshToken
            }
        }
    }
})
