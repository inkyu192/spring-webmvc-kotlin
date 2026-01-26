package spring.webmvc.infrastructure.config

import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
class LocalStackTestContainerConfig {

    companion object {
        @Container
        val localStackContainer: LocalStackContainer =
            LocalStackContainer(DockerImageName.parse("localstack/localstack:3.8.1"))
                .withServices(
                    LocalStackContainer.Service.S3,
                    LocalStackContainer.Service.DYNAMODB,
                    LocalStackContainer.Service.SQS,
                )
                .withEnv("DEFAULT_REGION", "ap-northeast-2")
                .apply { start() }
    }
}