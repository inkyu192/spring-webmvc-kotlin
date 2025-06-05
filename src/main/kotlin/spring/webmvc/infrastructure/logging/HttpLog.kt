package spring.webmvc.infrastructure.logging

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.StandardCharsets

@Component
class HttpLog(
    private val objectMapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(HttpLog::class.java)

    fun write(
        requestWrapper: ContentCachingRequestWrapper,
        responseWrapper: ContentCachingResponseWrapper,
        startTime: Long,
        endTime: Long,
    ) {
        val message = """
            |[REQUEST] ${"\n ${requestWrapper.method} ${requestWrapper.requestURI} (${(endTime - startTime) / 1000.0} s)"}
            |[CLIENT_IP] ${"\n ${requestWrapper.remoteAddr}"}
            |[REQUEST_HEADER] ${extractHeaders(request = requestWrapper)}
            |[REQUEST_PARAMETER] ${extractParameters(request = requestWrapper)}
            |[REQUEST_BODY] ${readBody(content = requestWrapper.contentAsByteArray)}
            |[RESPONSE_BODY] ${readBody(content = responseWrapper.contentAsByteArray)}
        """.trimMargin()

        logger.info("\n$message")
    }

    private fun extractHeaders(request: HttpServletRequest): String {
        val builder = StringBuilder()
        val names = request.headerNames
        while (names.hasMoreElements()) {
            val name = names.nextElement()
            val value = request.getHeader(name)
            builder.append("\n  $name: $value")
        }
        return builder.toString()
    }

    private fun extractParameters(request: HttpServletRequest): String {
        val builder = StringBuilder()
        val names = request.parameterNames
        while (names.hasMoreElements()) {
            val name = names.nextElement()
            val value = request.getParameter(name)
            builder.append("\n  $name: $value")
        }
        return builder.toString()
    }

    private fun readBody(content: ByteArray): String {
        if (content.isEmpty()) return ""

        val body = String(bytes = content, charset = StandardCharsets.UTF_8)
        return try {
            val json = objectMapper.readValue(body, Any::class.java)
            val writer = objectMapper.writerWithDefaultPrettyPrinter()
            "\n ${writer.writeValueAsString(json)}"
        } catch (e: Exception) {
            body
        }
    }
}
