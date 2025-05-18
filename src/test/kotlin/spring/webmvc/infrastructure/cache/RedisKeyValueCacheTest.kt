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

    beforeTest {
        redisTemplate.connectionFactory?.connection?.serverCommands()?.flushAll()
    }

    describe("setIfAbsent") {
        context("value 있을 경우") {
            it("false 반환한다") {
                val key = "testKey"
                val value = "testValue"
                val duration = Duration.ofSeconds(1)

                redisKeyValueCache.setIfAbsent(key = key, value = value, timeout = duration)

                // When
                val result = redisKeyValueCache.setIfAbsent(key = key, value = value, timeout = duration)

                result shouldBe false
            }
        }

        context("value 없을 경우") {
            it("저장 후 true 반환한다") {
                val key = "testKey"
                val value = "testValue"
                val duration = Duration.ofSeconds(1)

                redisKeyValueCache.setIfAbsent(key = key, value = value, timeout = duration)

                Thread.sleep(1000)

                val result = redisKeyValueCache.setIfAbsent(key = key, value = value, timeout = duration)

                result shouldBe true
            }
        }
    }

    describe("get") {
        context("존재하는 key의 경우") {
            it("value를 반환한다") {
                val key = "testKey"
                val value = "testValue"
                redisKeyValueCache.set(key = key, value = value)

                val result = redisKeyValueCache.get(key = key)

                result shouldBe value
            }
        }

        context("존재하지 않는 key의 경우") {
            it("null을 반환한다") {
                val key = "nonExistentKey"

                val result = redisKeyValueCache.get(key = key)

                result shouldBe null
            }
        }
    }

    describe("set") {
        context("key-value 저장") {
            it("key-value를 저장한다") {
                val key = "testKey"
                val value = "testValue"

                redisKeyValueCache.set(key = key, value = value)

                val result = redisKeyValueCache.get(key = key)
                result shouldBe value
            }
        }

        context("기존 key가 있을 경우") {
            it("value를 덮어쓴다") {
                val key = "testKey"
                val value1 = "testValue1"
                val value2 = "testValue2"
                redisKeyValueCache.set(key = key, value = value1)

                redisKeyValueCache.set(key = key, value = value2)

                val result = redisKeyValueCache.get(key = key)
                result shouldBe value2
            }
        }

        context("timeout 설정과 함께 저장") {
            it("key-value를 저장한다") {
                val key = "testKey"
                val value = "testValue"
                val duration = Duration.ofSeconds(1)

                redisKeyValueCache.set(key = key, value = value, timeout = duration)

                val result = redisKeyValueCache.get(key = key)
                result shouldBe value
            }

            it("timeout 이후에는 key-value가 만료된다") {
                val key = "testKey"
                val value = "testValue"
                val duration = Duration.ofSeconds(1)
                redisKeyValueCache.set(key = key, value = value, timeout = duration)

                Thread.sleep(1000)
                val result = redisKeyValueCache.get(key = key)

                result shouldBe null
            }
        }
    }

    describe("delete") {
        context("존재하는 key의 경우") {
            it("삭제하고 true를 반환한다") {
                val key = "testKey"
                val value = "testValue"
                redisKeyValueCache.set(key = key, value = value)

                val result = redisKeyValueCache.delete(key = key)

                result shouldBe true
                redisKeyValueCache.get(key = key) shouldBe null
            }
        }

        context("존재하지 않는 key의 경우") {
            it("false를 반환한다") {
                val key = "nonExistentKey"

                val result = redisKeyValueCache.delete(key = key)

                result shouldBe false
            }
        }
    }

    describe("increment") {
        it("숫자 값을 증가시키고 증가된 값을 반환한다") {
            val key = "counterKey"
            val delta = 5L
            redisKeyValueCache.set(key = key, value = "10")

            val result = redisKeyValueCache.increment(key = key, delta = delta)

            result shouldBe 15
            redisKeyValueCache.get(key = key) shouldBe "15"
        }
    }

    describe("decrement") {
        context("존재하는 key의 경우") {
            it("숫자 값을 감소시키고 감소된 값을 반환한다") {
                val key = "counterKey"
                val delta = 5L
                redisKeyValueCache.set(key = key, value = "10")

                val result = redisKeyValueCache.decrement(key = key, delta = delta)

                result shouldBe 5
                redisKeyValueCache.get(key = key) shouldBe "5"
            }
        }

        context("존재하지 않는 key의 경우") {
            it("-delta 값으로 초기화한다") {
                val key = "newCounterKey"
                val delta = 5L

                val result = redisKeyValueCache.decrement(key = key, delta = delta)

                result shouldBe -delta
                redisKeyValueCache.get(key = key) shouldBe "-5"
            }
        }
    }
})
