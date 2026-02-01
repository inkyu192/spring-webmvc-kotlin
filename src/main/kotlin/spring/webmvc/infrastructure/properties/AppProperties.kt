package spring.webmvc.infrastructure.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "app")
class AppProperties(
    val baseUrl: String,
    val docsUrl: String,
    val jwt: JwtProperties,
    val crypto: CryptoProperties,
    val cors: CorsProperties,
    val aws: AwsProperties,
    val external: ExternalProperties,
) {
    data class JwtProperties(
        val accessToken: TokenProperties,
        val refreshToken: TokenProperties,
    ) {
        data class TokenProperties(
            val key: String,
            val expiration: Duration,
        )
    }

    data class CryptoProperties(
        val secretKey: String,
        val ivParameter: String,
    )

    data class CorsProperties(
        val allowedOrigins: List<String>,
        val allowedOriginPatterns: List<String>,
        val allowedMethods: List<String>,
        val allowedHeaders: List<String>,
    )

    data class AwsProperties(
        val s3: S3Properties,
        val dynamodb: DynamoDbProperties,
        val cloudfront: CloudFrontProperties,
    ) {
        data class S3Properties(
            val endpoint: String,
            val bucket: String,
        )

        data class DynamoDbProperties(
            val endpoint: String,
        )

        data class CloudFrontProperties(
            val domain: String,
        )
    }

    data class ExternalProperties(
        val notification: NotificationProperties,
    ) {
        data class NotificationProperties(
            val host: String,
        )
    }
}
