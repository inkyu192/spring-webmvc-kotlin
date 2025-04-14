package spring.webmvc.infrastructure.config.redis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@Configuration(proxyBeanMethods = false)
class RedisConfig(
    private val redisProperties: RedisProperties,
) {
    @Bean
    fun redisConnectionFactory() = LettuceConnectionFactory(redisProperties.host, redisProperties.port)
}