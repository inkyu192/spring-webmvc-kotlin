package spring.webmvc.infrastructure.config

import jakarta.servlet.Filter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import spring.webmvc.infrastructure.logging.HttpLogFilter

@Configuration(proxyBeanMethods = false)
class FilterConfig {

    @Bean
    fun filterRegistrationBean() =
        FilterRegistrationBean<Filter>().apply {
            filter = HttpLogFilter()
            order = Int.MIN_VALUE
            addUrlPatterns("/*")
        }
}