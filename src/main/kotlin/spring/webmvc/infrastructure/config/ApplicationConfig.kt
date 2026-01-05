package spring.webmvc.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import spring.webmvc.application.strategy.ProductAttributeStrategy
import spring.webmvc.domain.model.enums.Category

@Configuration
class ApplicationConfig {
    @Bean
    fun productStrategyMap(productStrategies: List<ProductAttributeStrategy>): Map<Category, ProductAttributeStrategy> =
        productStrategies.associateBy { it.category() }
}
