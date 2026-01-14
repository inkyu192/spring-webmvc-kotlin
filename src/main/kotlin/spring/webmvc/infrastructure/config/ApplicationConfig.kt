package spring.webmvc.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import spring.webmvc.application.strategy.ProductPropertyStrategy
import spring.webmvc.domain.model.enums.ProductCategory

@Configuration
class ApplicationConfig {
    @Bean
    fun productStrategyMap(productStrategies: List<ProductPropertyStrategy>): Map<ProductCategory, ProductPropertyStrategy> =
        productStrategies.associateBy { it.category() }
}
