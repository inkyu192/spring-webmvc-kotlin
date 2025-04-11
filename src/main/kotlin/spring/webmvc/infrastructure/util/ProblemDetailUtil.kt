package spring.webmvc.infrastructure.util

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.net.URI

@Component
class ProblemDetailUtil(
    private val request: HttpServletRequest
) {
    fun createType(statusCode: Int): URI? {
        val status = HttpStatus.resolve(statusCode) ?: return null
        return createType(status)
    }

    fun createType(status: HttpStatus): URI {
        val baseUrl = getBaseUrl()
        val typeUri = "$baseUrl/docs/index.html#${status.name}"
        return URI.create(typeUri)
    }

    private fun getBaseUrl(): String {
        val scheme = request.scheme
        val serverName = request.serverName
        val serverPort = request.serverPort

        val isDefaultPort = (scheme == "http" && serverPort == 80) || (scheme == "https" && serverPort == 443)

        return if (isDefaultPort) "$scheme://$serverName" else "$scheme://$serverName:$serverPort"
    }
}