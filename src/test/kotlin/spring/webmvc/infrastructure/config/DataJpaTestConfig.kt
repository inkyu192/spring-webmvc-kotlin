package spring.webmvc.infrastructure.config

import org.mockito.kotlin.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import spring.webmvc.infrastructure.crypto.CryptoService

@TestConfiguration
class DataJpaTestConfig {

    @Bean
    fun cryptoUtil() = mock<CryptoService>()
}