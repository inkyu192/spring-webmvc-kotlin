package spring.webmvc.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import java.net.URI

@Configuration(proxyBeanMethods = false)
class AwsConfig(
    private val environment: Environment,
) {
    @Bean
    fun awsCredentialsProvider(): AwsCredentialsProvider {
        if (isLocal()) {
            return StaticCredentialsProvider.create(
                AwsBasicCredentials.create("accessKey", "secretKey")
            )
        }
        return DefaultCredentialsProvider.create()
    }

    @Bean
    fun s3Client(awsCredentialsProvider: AwsCredentialsProvider): S3Client {
        val builder = S3Client.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(awsCredentialsProvider)

        if (isLocal()) {
            builder
                .endpointOverride(URI.create("http://localhost:4566"))
                .serviceConfiguration(
                    S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build()
                )
        }

        return builder.build()
    }

    fun isLocal() = environment.activeProfiles.contains("local")
}