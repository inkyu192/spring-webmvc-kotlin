package spring.webmvc.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import spring.webmvc.infrastructure.util.SecurityContextUtil
import java.util.*

@EnableJpaAuditing
@Configuration(proxyBeanMethods = false)
class JpaConfig {

    @Bean
    fun auditorProvider() = AuditorAware { Optional.ofNullable(SecurityContextUtil.getMemberIdOrNull()) }
}