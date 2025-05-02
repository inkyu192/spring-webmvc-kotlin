package spring.webmvc.infrastructure.config

import org.mockito.Mockito
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import spring.webmvc.infrastructure.common.ResponseWriter
import spring.webmvc.infrastructure.common.UriFactory
import spring.webmvc.infrastructure.config.security.JwtProvider

@TestConfiguration
class WebMvcTestConfig {

    @Bean
    fun jwtProvider(): JwtProvider = Mockito.mock()

    @Bean
    fun responseWriter(): ResponseWriter = Mockito.mock()

    @Bean
    fun problemDetailUtil(): UriFactory = Mockito.mock()
}