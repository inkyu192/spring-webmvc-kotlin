package spring.webmvc.infrastructure.config

import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import spring.webmvc.infrastructure.config.security.JwtProvider
import spring.webmvc.infrastructure.common.UriFactory
import spring.webmvc.infrastructure.common.ResponseWriter

@TestConfiguration
class WebMvcTestConfig {

    @Bean
    fun jwtProvider() = mockk<JwtProvider>()

    @Bean
    fun responseWriter() = mockk<ResponseWriter>()

    @Bean
    fun problemDetailUtil() = mockk<UriFactory>()
}