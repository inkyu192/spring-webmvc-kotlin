package spring.webmvc.infrastructure.config

import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
class LocalStackTestConfig {

    companion object {
        @Container
        val localStackContainer: LocalStackContainer =
            LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
                .withServices(
                    LocalStackContainer.Service.S3
                )
                .withEnv("DEFAULT_REGION", "ap-northeast-2")
                .apply { start() }
    }
}