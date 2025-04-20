package spring.webmvc.infrastructure.persistence

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.RedisTemplate
import spring.webmvc.infrastructure.config.RedisTestContainerConfig
import spring.webmvc.infrastructure.persistence.RequestLockRedisRepository

@DataRedisTest
@Import(RedisTestContainerConfig::class)
class RequestLockRedisRepositoryTest(
    private val redisTemplate: RedisTemplate<String, String>
) : DescribeSpec({
    val requestLockRedisRepository = RequestLockRedisRepository(redisTemplate)

    describe("setIfAbsent 는") {
        val memberId = 1L
        val method = "GET"
        val uri = "/members"

        context("기존 데이터가 있을 경우") {
            it("저장하지 않는다") {
                requestLockRedisRepository.setIfAbsent(memberId = memberId, method = method, uri = uri)

                requestLockRedisRepository.setIfAbsent(memberId = memberId, method = method, uri = uri).apply {
                    this shouldBe false
                }
            }
        }

        context("기존 데이터가 없을 경우") {
            it("저장한다") {
                requestLockRedisRepository.setIfAbsent(memberId = memberId, method = method, uri = uri)

                Thread.sleep(1000)

                requestLockRedisRepository.setIfAbsent(memberId = memberId, method = method, uri = uri).apply {
                    this shouldBe true
                }
            }
        }
    }
})
