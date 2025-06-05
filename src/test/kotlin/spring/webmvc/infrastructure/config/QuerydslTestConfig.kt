package spring.webmvc.infrastructure.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class QuerydslTestConfig {

    @Bean
    fun jpaQueryFactory(entityManager: EntityManager) = JPAQueryFactory(entityManager)
}