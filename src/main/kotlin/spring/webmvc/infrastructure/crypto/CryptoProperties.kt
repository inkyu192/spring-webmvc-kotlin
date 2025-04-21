package spring.webmvc.infrastructure.crypto

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "crypto")
class CryptoProperties(
    val secretKey: String,
    val ivParameter: String,
)