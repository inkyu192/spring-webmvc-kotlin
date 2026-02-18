package spring.webmvc.presentation.exception

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.JwtException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import spring.webmvc.infrastructure.properties.AppProperties
import spring.webmvc.presentation.exception.handler.JwtExceptionHandler

class JwtExceptionHandlerTest {
    private val filterChain = mockk<FilterChain>(relaxed = true)
    private val appProperties = mockk<AppProperties>()
    private val objectMapper = mockk<ObjectMapper>()
    private val jwtExceptionHandler = JwtExceptionHandler(
        appProperties = appProperties,
        objectMapper = objectMapper,
    )
    private lateinit var request: MockHttpServletRequest
    private lateinit var response: MockHttpServletResponse

    @BeforeEach
    fun setUp() {
        request = MockHttpServletRequest()
        response = MockHttpServletResponse()
    }

    @Test
    @DisplayName("JwtException 발생할 경우 ProblemDetail 반환한다")
    fun doFilterWhenJwtExceptionOccurs() {
        val status = HttpStatus.UNAUTHORIZED
        val message = "JwtException"
        val docsUrl = "http://localhost:8080/docs/index.html"

        val problemDetailJson =
            """{"type":"$docsUrl#${status.name}","title":"Unauthorized","status":401,"detail":"$message"}"""

        every { filterChain.doFilter(request, response) } throws JwtException(message)
        every { appProperties.docsUrl } returns docsUrl
        every { objectMapper.writeValueAsString(any<ProblemDetail>()) } returns problemDetailJson

        jwtExceptionHandler.doFilter(request, response, filterChain)

        verify { objectMapper.writeValueAsString(any<ProblemDetail>()) }
    }

    @Test
    @DisplayName("RuntimeException 발생할 경우 ProblemDetail 반환한다")
    fun doFilterWhenRuntimeExceptionOccurs() {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val message = "RuntimeException"
        val docsUrl = "http://localhost:8080/docs/index.html"

        val problemDetailJson =
            """{"type":"$docsUrl#${status.name}","title":"Internal Server Error","status":500,"detail":"$message"}"""

        every { filterChain.doFilter(request, response) } throws RuntimeException(message)
        every { appProperties.docsUrl } returns docsUrl
        every { objectMapper.writeValueAsString(any<ProblemDetail>()) } returns problemDetailJson

        jwtExceptionHandler.doFilter(request, response, filterChain)

        verify { objectMapper.writeValueAsString(any<ProblemDetail>()) }
    }
}
