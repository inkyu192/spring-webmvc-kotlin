package spring.webmvc.infrastructure.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cors")
class CorsProperties(
    val allowedOrigins: List<String>,
    val allowedOriginPatterns: List<String>,
    val allowedMethods: List<String>,
    val allowedHeaders: List<String>,
)