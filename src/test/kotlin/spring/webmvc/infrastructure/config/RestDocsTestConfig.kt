package spring.webmvc.infrastructure.config

import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.restdocs.operation.preprocess.Preprocessors

@TestConfiguration
class RestDocsTestConfig {

    @Bean
    fun restDocsMockMvcConfigurationCustomizer() = RestDocsMockMvcConfigurationCustomizer { configurer ->
        configurer.operationPreprocessors()
            .withRequestDefaults(Preprocessors.prettyPrint())
            .withResponseDefaults(Preprocessors.prettyPrint())
    }
}