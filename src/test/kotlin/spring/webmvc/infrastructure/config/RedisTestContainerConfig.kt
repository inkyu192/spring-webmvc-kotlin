package spring.webmvc.infrastructure.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration

@Testcontainers
@TestConfiguration
class RedisTestContainerConfig {

    companion object {
        private const val REDIS_PORT = 6379

        @Container
        private val redisContainer = GenericContainer<Nothing>("redis:latest").apply {
            withExposedPorts(REDIS_PORT)
            waitingFor(Wait.forListeningPort())
            withStartupTimeout(Duration.ofSeconds(60))
            start()
        }
    }

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(
            redisContainer.host,
            redisContainer.getMappedPort(REDIS_PORT)
        )
    }
}