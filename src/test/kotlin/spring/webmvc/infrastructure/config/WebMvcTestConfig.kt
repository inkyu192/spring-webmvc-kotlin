package spring.webmvc.infrastructure.config

import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import spring.webmvc.infrastructure.common.ResponseWriter
import spring.webmvc.infrastructure.common.UriFactory
import spring.webmvc.infrastructure.logging.HttpLog
import spring.webmvc.infrastructure.security.JwtProvider

@TestConfiguration
class WebMvcTestConfig {

    @Bean
    fun jwtProvider() = mockk<JwtProvider>()

    @Bean
    fun responseWriter() = mockk<ResponseWriter>()

    @Bean
    fun problemDetailUtil() = mockk<UriFactory>()

    @Bean
    fun httpLog() = mockk<HttpLog>()
}