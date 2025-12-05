package spring.webmvc.infrastructure.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.FilterChain
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder

class JwtAuthenticationFilterTest {
    private val filterChain = mockk<FilterChain>(relaxed = true)
    private val jwtProvider = mockk<JwtProvider>()
    private val jwtAuthenticationFilter = JwtAuthenticationFilter(jwtProvider)
    private lateinit var request: MockHttpServletRequest
    private lateinit var response: MockHttpServletResponse

    @BeforeEach
    fun setUp() {
        SecurityContextHolder.clearContext()
        request = MockHttpServletRequest()
        response = MockHttpServletResponse()
    }

    @Test
    @DisplayName("Authorization 없을 경우 Authentication 을 생성하지 않는다")
    fun doFilterWhenAuthorizationIsAbsent() {
        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        Assertions.assertThat(SecurityContextHolder.getContext().authentication).isNull()
    }

    @Test
    @DisplayName("Authorization 비어있을 경우 Authentication 을 생성하지 않는다")
    fun doFilterWhenAuthorizationIsEmpty() {
        request.addHeader("Authorization", "")

        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        Assertions.assertThat(SecurityContextHolder.getContext().authentication).isNull()
    }

    @Test
    @DisplayName("Authorization 있고 유효성 검사 실패할 경우 JwtException 발생한다")
    fun doFilterWhenTokenValidationFails() {
        every { jwtProvider.parseAccessToken(any()) } throws JwtException("invalidToken")

        request.addHeader("Authorization", "Bearer invalid.jwt.token")

        Assertions.assertThatThrownBy { jwtAuthenticationFilter.doFilter(request, response, filterChain) }
            .isInstanceOf(JwtException::class.java)
    }

    @Test
    @DisplayName("Authorization 있고 유효성 검사 성공할 경우 Authentication 생성한다")
    fun doFilterWhenTokenValidationSucceeds() {
        val token = "valid.jwt.token"
        val claims = mockk<Claims>(relaxed = true)
        val memberId = 1L
        val permissions = listOf("PRODUCT_READER")

        every { jwtProvider.parseAccessToken(any()) } returns claims
        every { claims["memberId"] } returns memberId
        every { claims["permissions", List::class.java] } returns permissions

        request.addHeader("Authorization", "Bearer $token")

        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        Assertions.assertThat(SecurityContextHolder.getContext().authentication).isNotNull()
        Assertions.assertThat(SecurityContextHolder.getContext().authentication.credentials).isEqualTo(token)
    }
}