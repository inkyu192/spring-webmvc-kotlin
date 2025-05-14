package spring.webmvc.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.util.pattern.PathPatternParser

@Configuration(proxyBeanMethods = false)
class WebConfig {

    @Bean
    fun pathPatternParser() = PathPatternParser()
}