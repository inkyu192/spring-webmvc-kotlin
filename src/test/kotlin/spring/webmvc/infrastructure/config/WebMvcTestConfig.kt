package spring.webmvc.infrastructure.config

import org.mockito.kotlin.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import spring.webmvc.infrastructure.common.ResponseWriter
import spring.webmvc.infrastructure.common.UriFactory
import spring.webmvc.infrastructure.config.security.JwtProvider

@TestConfiguration
class WebMvcTestConfig {

    @Bean
    fun jwtProvider() = mock<JwtProvider>()

    @Bean
    fun responseWriter() = mock<ResponseWriter>()

    @Bean
    fun problemDetailUtil() = mock<UriFactory>()
}