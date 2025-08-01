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
import spring.webmvc.domain.cache.CacheKey
import spring.webmvc.domain.cache.ValueCache
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.infrastructure.security.JwtProvider

class AuthServiceTest : DescribeSpec({
    val jwtProvider = mockk<JwtProvider>()
    val valueCache = mockk<ValueCache>()
    val memberRepository = mockk<MemberRepository>()
    val passwordEncoder = mockk<PasswordEncoder>()
    val authService = AuthService(
        jwtProvider = jwtProvider,
        valueCache = valueCache,
        memberRepository = memberRepository,
        passwordEncoder = passwordEncoder
    )

    describe("login") {
        context("Member 엔티티 없을 경우") {
            it("BadCredentialsException 발생한다") {
                val email = "test@gmail.com"
                val password = "password"

                every { memberRepository.findByEmail(email = any<Email>()) } returns null

                shouldThrow<BadCredentialsException> { authService.login(email = email, password = password) }
            }
        }

        context("비밀번호가 일치하지 않을 경우") {
            it("BadCredentialsException 발생한다") {
                val email = "test@gmail.com"
                val password = "password"
                val member = mockk<Member>(relaxed = true)

                every { memberRepository.findByEmail(email = any<Email>()) } returns member
                every { passwordEncoder.matches(password, member.password) } returns false

                shouldThrow<BadCredentialsException> { authService.login(email = email, password = password) }
            }
        }

        context("유효성 검사 성공할 경우") {
            it("Token 저장 후 반환한다") {
                val email = "test@gmail.com"
                val password = "password"
                val accessToken = "accessToken"
                val refreshToken = "refreshToken"

                val member = mockk<Member>(relaxed = true)

                every { memberRepository.findByEmail(email = any<Email>()) } returns member
                every { passwordEncoder.matches(any(), any()) } returns true
                every { jwtProvider.createAccessToken(memberId = any(), permissions = any()) } returns accessToken
                every { jwtProvider.createRefreshToken() } returns refreshToken
                every { valueCache.set(key = any(), value = any<String>(), timeout = any()) } returns Unit

                val result = authService.login(email = email, password = password)

                verify(exactly = 1) { valueCache.set(key = any(), value = any<String>(), timeout = any()) }
                result.accessToken shouldBe accessToken
                result.refreshToken shouldBe refreshToken
            }
        }
    }

    describe("refreshToken") {
        context("accessToken 유효하지 않을 경우") {
            it("JwtException 발생한다") {
                val accessToken = "accessToken"
                val refreshToken = "refreshToken"

                every { jwtProvider.parseAccessToken(accessToken) } throws JwtException("invalid access token")

                shouldThrow<JwtException> {
                    authService.refreshToken(accessToken = accessToken, refreshToken = refreshToken)
                }
            }
        }

        context("refreshToken 유효하지 않을 경우") {
            it("JwtException 발생한다") {
                val memberId = 1L
                val accessToken = "accessToken"
                val refreshToken = "refreshToken"
                val claims = mockk<Claims>()

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
                val memberId = 1L
                val accessToken = "accessToken"
                val fakeRefreshToken = "fakeRefreshToken"
                val refreshToken = "refreshToken"
                val claims = mockk<Claims>()
                val member = mockk<Member>()

                every { jwtProvider.parseAccessToken(accessToken) } returns claims
                every { memberRepository.findByIdOrNull(memberId) } returns member
                every { claims["memberId"] } returns memberId
                every { jwtProvider.parseRefreshToken(fakeRefreshToken) } returns claims
                every { valueCache.get(CacheKey.REFRESH_TOKEN.generate(memberId)) } returns refreshToken

                shouldThrow<BadCredentialsException> {
                    authService.refreshToken(accessToken = accessToken, refreshToken = fakeRefreshToken)
                }
            }
        }

        context("유효성 검사 성공할 경우") {
            it("AccessToken 갱신된다") {
                val memberId = 1L
                val accessToken = "accessToken"
                val newAccessToken = "newAccessToken"
                val refreshToken = "refreshToken"

                val claims = mockk<Claims>()
                val member = mockk<Member>(relaxed = true)

                every { jwtProvider.parseAccessToken(accessToken) } returns claims
                every { memberRepository.findByIdOrNull(memberId) } returns member
                every { claims["memberId"] } returns memberId
                every { jwtProvider.parseRefreshToken(refreshToken) } returns claims
                every { valueCache.get(CacheKey.REFRESH_TOKEN.generate(memberId)) } returns refreshToken
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
