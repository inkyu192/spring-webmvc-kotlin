package spring.webmvc.presentation.exception

import io.jsonwebtoken.JwtException
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import spring.webmvc.infrastructure.common.UriFactory
import spring.webmvc.infrastructure.common.ResponseWriter
import spring.webmvc.presentation.exception.handler.JwtExceptionHandler
import java.net.URI

class JwtExceptionHandlerTest : DescribeSpec({
    val filterChain = mockk<FilterChain>(relaxed = true)
    val uriFactory = mockk<UriFactory>()
    val responseWriter = mockk<ResponseWriter>(relaxed = true)
    val jwtExceptionHandler = JwtExceptionHandler(
        uriFactory = uriFactory,
        responseWriter = responseWriter
    )
    lateinit var request: MockHttpServletRequest
    lateinit var response: MockHttpServletResponse

    beforeEach {
        request = MockHttpServletRequest()
        response = MockHttpServletResponse()
    }

    describe("ExceptionHandlerFilter 는") {
        context("JwtException 발생할 경우") {
            it("UNAUTHORIZED 반환한다") {
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
        }

        context("RuntimeException 발생할 경우") {
            it("INTERNAL_SERVER_ERROR 반환한다") {
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
    }
})
