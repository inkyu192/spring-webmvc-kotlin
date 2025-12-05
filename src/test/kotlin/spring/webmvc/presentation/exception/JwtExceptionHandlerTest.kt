package spring.webmvc.presentation.exception

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
import spring.webmvc.infrastructure.common.ResponseWriter
import spring.webmvc.infrastructure.common.UriFactory
import spring.webmvc.presentation.exception.handler.JwtExceptionHandler
import java.net.URI

class JwtExceptionHandlerTest {
    private val filterChain = mockk<FilterChain>(relaxed = true)
    private val uriFactory = mockk<UriFactory>()
    private val responseWriter = mockk<ResponseWriter>(relaxed = true)
    private val jwtExceptionHandler = JwtExceptionHandler(
        uriFactory = uriFactory,
        responseWriter = responseWriter,
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
        val uri = URI.create("uri")

        every { filterChain.doFilter(request, response) } throws JwtException(message)
        every { uriFactory.createApiDocUri(status) } returns uri

        val problemDetail = ProblemDetail.forStatusAndDetail(status, message)
        problemDetail.type = uri

        jwtExceptionHandler.doFilter(request, response, filterChain)

        verify { responseWriter.writeResponse(problemDetail) }
    }

    @Test
    @DisplayName("RuntimeException 발생할 경우 ProblemDetail 반환한다")
    fun doFilterWhenRuntimeExceptionOccurs() {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val message = "RuntimeException"
        val uri = URI.create("uri")

        every { filterChain.doFilter(request, response) } throws RuntimeException(message)
        every { uriFactory.createApiDocUri(status) } returns uri

        val problemDetail = ProblemDetail.forStatusAndDetail(status, message)
        problemDetail.type = uri

        jwtExceptionHandler.doFilter(request, response, filterChain)

        verify { responseWriter.writeResponse(problemDetail) }
    }
}
