package spring.webmvc.infrastructure.config

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.MongoTemplate
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration

@Testcontainers
@TestConfiguration
class MongoTestContainerConfig {

    companion object {
        private const val MONGO_PORT = 27017

        @Container
        private val mongoDBContainer = MongoDBContainer("mongo:latest").apply {
            withExposedPorts(MONGO_PORT)
            waitingFor(Wait.forListeningPort())
            withStartupTimeout(Duration.ofSeconds(60))
            start()
        }
    }

    @Bean
    fun mongoClient() = MongoClients.create(mongoDBContainer.replicaSetUrl)

    @Bean
    fun mongoTemplate(mongoClient: MongoClient) = MongoTemplate(mongoClient, "test-db")
}