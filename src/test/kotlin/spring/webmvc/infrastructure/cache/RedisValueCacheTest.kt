package spring.webmvc.infrastructure.cache

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.RedisTemplate
import spring.webmvc.infrastructure.config.RedisTestContainerConfig
import java.time.Duration

@DataRedisTest
@Import(RedisTestContainerConfig::class)
class RedisValueCacheTest(
    private val redisTemplate: RedisTemplate<String, String>,
) : DescribeSpec({
    val redisValueCache = RedisValueCache(redisTemplate = redisTemplate, objectMapper = ObjectMapper())

    beforeTest {
        redisTemplate.connectionFactory?.connection?.serverCommands()?.flushAll()
    }

    describe("setIfAbsent") {
        context("key-value 있을 경우") {
            it("false 반환한다") {
                val key = "testKey"
                val value = "testValue"
                val duration = Duration.ofMillis(1)

                redisTemplate.opsForValue().set(key, value)

                val result = redisValueCache.setIfAbsent(key = key, value = value, timeout = duration)

                result shouldBe false
            }
        }

        context("key-value 없을 경우") {
            it("true 반환한다") {
                val key = "testKey"
                val value = "testValue"
                val duration = Duration.ofMillis(1)

                val result = redisValueCache.setIfAbsent(key = key, value = value, timeout = duration)

                result shouldBe true
            }
        }
    }

    describe("get") {
        context("key-value 있을 경우") {
            it("value 반환한다") {
                val key = "testKey"
                val value = "testValue"

                redisTemplate.opsForValue().set(key, value)

                val result = redisValueCache.get(key = key)

                result shouldBe value
            }
        }

        context("key-value 없을 경우") {
            it("null 반환한다") {
                val key = "testKey"

                val result = redisValueCache.get(key = key)

                result shouldBe null
            }
        }
    }

    describe("set") {
        it("key-value 저장한다") {
            val key = "testKey"
            val value = "testValue"

            redisValueCache.set(key = key, value = value)

            val result = redisTemplate.opsForValue().get(key)

            result shouldBe value
        }

        context("duration 있을 경우") {
            it("duration 동안 key-value 저장한다") {
                val key = "testKey"
                val value = "testValue"
                val duration = Duration.ofMillis(1)

                redisValueCache.set(key = key, value = value, timeout = duration)

                redisTemplate.opsForValue().get(key) shouldBe value
                Thread.sleep(duration)
                redisTemplate.opsForValue().get(key) shouldBe null

            }
        }
    }

    describe("delete") {
        context("key-value 있을 경우") {
            it("삭제 후 true 반환한다") {
                val key = "testKey"
                val value = "testValue"

                redisTemplate.opsForValue().set(key, value)

                val result = redisValueCache.delete(key = key)

                result shouldBe true
                redisTemplate.opsForValue().get(key) shouldBe null
            }
        }

        context("key-value 없을 경우") {
            it("false 반환한다") {
                val key = "testKey"

                val result = redisValueCache.delete(key = key)

                result shouldBe false
            }
        }
    }

    describe("increment") {
        it("숫자 값을 증가시키고 증가된 값을 반환한다") {
            val key = "testKey"
            val delta = 5L

            redisTemplate.opsForValue().set(key, "10")

            val result = redisValueCache.increment(key = key, delta = delta)

            result shouldBe 15
            redisTemplate.opsForValue().get(key) shouldBe "15"
        }
    }

    describe("decrement") {
        it("숫자 값을 감소시키고 감소된 값을 반환한다") {
            val key = "testKey"
            val delta = 5L

            redisTemplate.opsForValue().set(key, "10")

            val result = redisValueCache.decrement(key = key, delta = delta)

            result shouldBe 5
            redisTemplate.opsForValue().get(key) shouldBe "5"
        }
    }
})
