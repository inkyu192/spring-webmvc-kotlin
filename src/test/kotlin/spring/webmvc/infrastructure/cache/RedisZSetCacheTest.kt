package spring.webmvc.infrastructure.cache

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.RedisTemplate
import spring.webmvc.infrastructure.config.RedisTestContainerConfig
import java.time.Duration

@DataRedisTest
@Import(RedisTestContainerConfig::class)
class RedisZSetCacheTest(
    private val redisTemplate: RedisTemplate<String, String>,
) : DescribeSpec({
    val redisZSetCache = RedisZSetCache(redisTemplate = redisTemplate, objectMapper = ObjectMapper())

    beforeTest {
        redisTemplate.connectionFactory?.connection?.serverCommands()?.flushAll()
    }

    describe("add") {
        it("key, value, score 저장한다") {
            val key = "testKey"
            val value = "testValue"
            val score = 1.0

            redisZSetCache.add(key = key, value = value, score = score)

            val result = redisTemplate.opsForZSet().range(key, 0, -1)
            result?.shouldContain(value)
        }
    }

    describe("expire") {
        it("duration 지나면 key 사라진다") {
            val key = "testKey"
            val value = "testValue"
            val score = 1.0
            val duration = Duration.ofMillis(100)
            redisTemplate.opsForZSet().add(key, value, score)

            redisZSetCache.expire(key = key, timeout = duration)

            redisTemplate.opsForZSet().range(key, 0, -1)?.shouldContain(value)
            Thread.sleep(duration)
            redisTemplate.opsForZSet().range(key, 0, -1)?.shouldBeEmpty()
        }
    }

    describe("range") {
        it("범위 조회 후 반환한다") {
            val key = "testKey"
            val value1 = "testValue1"
            val value2 = "testValue2"
            val value3 = "testValue3"

            redisTemplate.opsForZSet().add(key, value1, 1.0)
            redisTemplate.opsForZSet().add(key, value2, 2.0)
            redisTemplate.opsForZSet().add(key, value3, 3.0)

            val result = redisZSetCache.range(key = key, start = 0, end = 1, clazz = String::class.java)

            result shouldHaveSize 2
            result shouldContainAll listOf(value1, value2)
        }
    }
})
