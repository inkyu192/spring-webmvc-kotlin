package spring.webmvc.infrastructure.config

import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.querydsl.jpa.impl.JPAQueryFactory
import io.mockk.mockk
import jakarta.persistence.EntityManager
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import spring.webmvc.infrastructure.crypto.CryptoService

@TestConfiguration
class JpaTestConfig {

    @Bean
    fun cryptoUtil() = mockk<CryptoService>()

    @Bean
    fun jpaQueryFactory(entityManager: EntityManager) = JPAQueryFactory(entityManager)

    @Bean
    fun jpqlRenderContext() = JpqlRenderContext()
}