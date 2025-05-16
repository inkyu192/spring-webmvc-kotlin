package spring.webmvc.infrastructure.cache

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.RedisTemplate
import spring.webmvc.infrastructure.config.RedisTestContainerConfig
import java.time.Duration

@DataRedisTest
@Import(RedisTestContainerConfig::class)
class RedisKeyValueCacheTest(
    private val redisTemplate: RedisTemplate<String, String>,
) : DescribeSpec({
    val redisKeyValueCache = RedisKeyValueCache(redisTemplate)

    describe("setIfAbsent") {
        val key = "testKey"
        val value = "testValue"
        val duration = Duration.ofSeconds(1)

        context("value 있을 경우") {
            it("false 반환한다") {
                redisKeyValueCache.setIfAbsent(key = key, value = value, timeout = duration)

                // When
                val result = redisKeyValueCache.setIfAbsent(key = key, value = value, timeout = duration)

                result shouldBe false
            }
        }

        context("value 없을 경우") {
            it("저장 후 true 반환한다") {
                redisKeyValueCache.setIfAbsent(key = key, value = value, timeout = duration)

                Thread.sleep(1000)

                val result = redisKeyValueCache.setIfAbsent(key = key, value = value, timeout = duration)

                result shouldBe true
            }
        }
    }
})
