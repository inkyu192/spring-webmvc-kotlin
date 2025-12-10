package spring.webmvc.infrastructure.config

import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import spring.webmvc.infrastructure.security.SecurityContextUtil
import java.util.*

@EnableJpaAuditing
@Configuration(proxyBeanMethods = false)
class JpaConfig {

    @Bean
    fun auditorProvider() = AuditorAware { Optional.ofNullable(SecurityContextUtil.getUserIdOrNull()) }

    @Bean
    fun jpaQueryFactory(entityManager: EntityManager) = JPAQueryFactory(entityManager)

    @Bean
    fun jpqlRenderContext() = JpqlRenderContext()
}