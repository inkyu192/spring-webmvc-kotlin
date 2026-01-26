package spring.webmvc.infrastructure.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "external")
data class ExternalProperties(
    val notification: NotificationProperties,
) {
    data class NotificationProperties(
        val host: String,
    )
}
