package spring.webmvc.infrastructure.config

import com.querydsl.jpa.impl.JPAQueryFactory
import io.mockk.mockk
import jakarta.persistence.EntityManager
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import spring.webmvc.infrastructure.util.crypto.CryptoUtil

@TestConfiguration
class DataJpaTestConfig {

    @Bean
    fun jpaQueryFactory(entityManager: EntityManager): JPAQueryFactory = JPAQueryFactory(entityManager)

    @Bean
    fun cryptoUtil() = mockk<CryptoUtil>()
}