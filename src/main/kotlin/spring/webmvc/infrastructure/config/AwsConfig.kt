package spring.webmvc.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import spring.webmvc.infrastructure.properties.AppProperties
import java.net.URI

@Configuration(proxyBeanMethods = false)
class AwsConfig(
    private val appProperties: AppProperties,
) {
    @Bean
    fun awsCredentialsProvider(): AwsCredentialsProvider {
        return StaticCredentialsProvider.create(
            AwsBasicCredentials.create("accessKey", "secretKey")
        )
    }

    @Bean
    fun s3Client(awsCredentialsProvider: AwsCredentialsProvider): S3Client =
        S3Client.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(awsCredentialsProvider)
            .endpointOverride(URI.create(appProperties.aws.s3.endpoint))
            .serviceConfiguration(
                S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .build()
            ).build()

    @Bean
    fun dynamoDbClient(awsCredentialsProvider: AwsCredentialsProvider): DynamoDbClient =
        DynamoDbClient.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(awsCredentialsProvider)
            .endpointOverride(URI.create(appProperties.aws.dynamodb.endpoint))
            .build()

    @Bean
    fun dynamoDbEnhancedClient(dynamoDbClient: DynamoDbClient): DynamoDbEnhancedClient =
        DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build()
}
