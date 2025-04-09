package spring.webmvc.presentation.infrastructure.config

import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import spring.webmvc.infrastructure.config.security.JwtTokenProvider
import spring.webmvc.infrastructure.util.ResponseWriter

@TestConfiguration
class WebMvcTestConfig {

    @Bean
    fun jwtTokenProvider() = mockk<JwtTokenProvider>()

    @Bean
    fun responseWriter() = mockk<ResponseWriter>()
}