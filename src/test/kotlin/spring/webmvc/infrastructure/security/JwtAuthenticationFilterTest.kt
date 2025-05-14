package spring.webmvc.infrastructure.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.FilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder

class JwtAuthenticationFilterTest : DescribeSpec({
    val filterChain = mockk<FilterChain>(relaxed = true)
    val jwtProvider = mockk<JwtProvider>()
    val jwtAuthenticationFilter = JwtAuthenticationFilter(jwtProvider)
    lateinit var request: MockHttpServletRequest
    lateinit var response: MockHttpServletResponse

    beforeEach {
        SecurityContextHolder.clearContext()
        request = MockHttpServletRequest()
        response = MockHttpServletResponse()
    }

    describe("doFilter") {
        context("Authorization 없을 경우") {
            it("Authentication 을 생성하지 않는다") {
                jwtAuthenticationFilter.doFilter(request, response, filterChain)

                SecurityContextHolder.getContext().authentication shouldBe null
            }
        }

        context("Authorization 비어있을 경우") {
            it("Authentication 을 생성하지 않는다") {
                request.addHeader("Authorization", "")

                jwtAuthenticationFilter.doFilter(request, response, filterChain)

                SecurityContextHolder.getContext().authentication shouldBe null
            }
        }

        context("Authorization 있고 유효성 검사 실패할 경우") {
            it("JwtException 발생한다") {
                every { jwtProvider.parseAccessToken(any()) } throws JwtException("invalidToken")

                request.addHeader("Authorization", "Bearer invalid.jwt.token")

                shouldThrow<JwtException> { jwtAuthenticationFilter.doFilter(request, response, filterChain) }
            }
        }

        context("Authorization 있고 유효성 검사 성공할 경우") {
            it("Authentication 생성한다") {
                val token = "valid.jwt.token"
                val claims = mockk<Claims>(relaxed = true)
                val memberId = 1L
                val permissions = listOf("ITEM_READ")

                every { jwtProvider.parseAccessToken(any()) } returns claims
                every { claims["memberId"] } returns memberId
                every { claims["permissions", List::class.java] } returns permissions

                request.addHeader("Authorization", "Bearer $token")

                jwtAuthenticationFilter.doFilter(request, response, filterChain)

                SecurityContextHolder.getContext().authentication shouldNotBe null
                SecurityContextHolder.getContext().authentication.credentials shouldBe token
            }
        }
    }
})