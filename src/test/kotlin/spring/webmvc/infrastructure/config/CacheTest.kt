package spring.webmvc.infrastructure.config

import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@DataRedisTest
@Import(RedisTestContainerConfig::class)
annotation class CacheTest
