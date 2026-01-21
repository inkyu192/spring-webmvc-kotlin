package spring.webmvc.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import spring.webmvc.application.strategy.ProductAttributeStrategy
import spring.webmvc.domain.model.enums.ProductCategory

@Configuration
class ApplicationConfig {

    @Bean
    fun productStrategyMap(
        productStrategies: List<ProductAttributeStrategy>,
    ): Map<ProductCategory, ProductAttributeStrategy> {
        val duplicates = productStrategies
            .groupBy { it.category() }
            .filter { it.value.size > 1 }
            .keys

        check(duplicates.isEmpty()) { "중복된 ProductAttributeStrategy가 존재합니다: $duplicates" }

        return productStrategies.associateBy { it.category() }
    }
}
