package spring.webmvc.infrastructure.logging

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.server.PathContainer
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import org.springframework.web.util.pattern.PathPatternParser

@Component
class HttpLogFilter(
    private val httpLog: HttpLog,
    pathPatternParser: PathPatternParser,
) : OncePerRequestFilter() {
    private val excludedPatterns = listOf(
        pathPatternParser.parse("/actuator/**"),
        pathPatternParser.parse("/favicon.ico"),
        pathPatternParser.parse("/static/**"),
        pathPatternParser.parse("/public/**"),
        pathPatternParser.parse("/docs/index.html"),
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        if (isLogExclude(request)) {
            filterChain.doFilter(request, response)
            return
        }

        val requestWrapper = ContentCachingRequestWrapper(request)
        val responseWrapper = ContentCachingResponseWrapper(response)

        val startTime = System.currentTimeMillis()
        filterChain.doFilter(requestWrapper, responseWrapper)
        val endTime = System.currentTimeMillis()

        httpLog.write(
            requestWrapper = requestWrapper,
            responseWrapper = responseWrapper,
            startTime = startTime,
            endTime = endTime,
        )

        responseWrapper.copyBodyToResponse()
    }

    private fun isLogExclude(request: HttpServletRequest): Boolean {
        val uri = request.requestURI
        val pathContainer = PathContainer.parsePath(uri)
        return excludedPatterns.any { it.matches(pathContainer) }
    }
}
