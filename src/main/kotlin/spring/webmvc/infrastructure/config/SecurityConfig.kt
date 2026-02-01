package spring.webmvc.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import spring.webmvc.infrastructure.properties.AppProperties
import spring.webmvc.infrastructure.security.JwtAuthenticationFilter
import spring.webmvc.presentation.exception.handler.AccessDeniedExceptionHandler
import spring.webmvc.presentation.exception.handler.AuthenticationExceptionHandler
import spring.webmvc.presentation.exception.handler.JwtExceptionHandler

@EnableMethodSecurity
@Configuration(proxyBeanMethods = false)
class SecurityConfig {

    @Bean
    fun securityFilterChain(
        httpSecurity: HttpSecurity,
        appProperties: AppProperties,
        authenticationExceptionHandler: AuthenticationExceptionHandler,
        accessDeniedExceptionHandler: AccessDeniedExceptionHandler,
        jwtAuthenticationFilter: JwtAuthenticationFilter,
        jwtExceptionHandler: JwtExceptionHandler,
    ): SecurityFilterChain = httpSecurity
        .csrf { it.disable() }
        .anonymous { it.disable() }
        .rememberMe { it.disable() }
        .logout { it.disable() }
        .httpBasic { it.disable() }
        .formLogin { it.disable() }
        .exceptionHandling {
            it.authenticationEntryPoint(authenticationExceptionHandler)
            it.accessDeniedHandler(accessDeniedExceptionHandler)
        }
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .cors { it.configurationSource(createCorsConfig(appProperties.cors)) }
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        .addFilterBefore(jwtExceptionHandler, jwtAuthenticationFilter.javaClass)
        .build()

    private fun createCorsConfig(corsProperties: AppProperties.CorsProperties): CorsConfigurationSource {
        val config = CorsConfiguration().apply {
            allowedOrigins = corsProperties.allowedOrigins
            allowedOriginPatterns = corsProperties.allowedOriginPatterns
            allowedMethods = corsProperties.allowedMethods
            allowedHeaders = corsProperties.allowedHeaders
            allowCredentials = true
        }

        return UrlBasedCorsConfigurationSource().apply { registerCorsConfiguration("/**", config) }
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}