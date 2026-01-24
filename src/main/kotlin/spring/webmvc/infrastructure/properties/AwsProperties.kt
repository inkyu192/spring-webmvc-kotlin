package spring.webmvc.infrastructure.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "aws")
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
