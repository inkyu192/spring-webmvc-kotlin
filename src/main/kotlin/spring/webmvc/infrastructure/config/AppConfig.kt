package spring.webmvc.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import spring.webmvc.application.strategy.ProductStrategy

@Configuration(proxyBeanMethods = false)
class AppConfig {

    @Bean
    fun productStrategyMap(productStrategies: List<ProductStrategy>) = productStrategies.associateBy { it.category() }
}