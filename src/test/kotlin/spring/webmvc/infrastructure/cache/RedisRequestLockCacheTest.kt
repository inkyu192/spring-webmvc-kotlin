package spring.webmvc.infrastructure.cache

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.RedisTemplate
import spring.webmvc.infrastructure.config.RedisTestContainerConfig

@DataRedisTest
@Import(RedisTestContainerConfig::class)
class RedisRequestLockCacheTest(
    private val redisTemplate: RedisTemplate<String, String>,
) : DescribeSpec({
    val redisRequestLockCache = RedisRequestLockCache(redisTemplate)

    describe("setIfAbsent") {
        val memberId = 1L
        val method = "GET"
        val uri = "/members"

        context("RequestLock 있을 경우") {
            it("false 반환한다") {
                redisRequestLockCache.setIfAbsent(memberId = memberId, method = method, uri = uri)

                val result = redisRequestLockCache.setIfAbsent(memberId = memberId, method = method, uri = uri)

                result shouldBe false
            }
        }

        context("RequestLock 없을 경우") {
            it("저장 후 true 반환한다") {
                redisRequestLockCache.setIfAbsent(memberId = memberId, method = method, uri = uri)

                Thread.sleep(1000)

                val result = redisRequestLockCache.setIfAbsent(memberId = memberId, method = method, uri = uri)

                result shouldBe true
            }
        }
    }
})
