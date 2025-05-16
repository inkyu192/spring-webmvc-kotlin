package spring.webmvc.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import spring.webmvc.domain.cache.CacheKey
import spring.webmvc.domain.cache.KeyValueCache
import spring.webmvc.presentation.exception.DuplicateRequestException

class RequestLockServiceTest : DescribeSpec({
    val keyValueCache = mockk<KeyValueCache>()
    val requestLockService = RequestLockService(keyValueCache)

    describe("validate") {
        context("RequestLock 없을 경우") {
            it("저장한다") {
                val memberId = 1L
                val method = "GET"
                val uri = "/members"

                val key = CacheKey.REQUEST_LOCK.generate(memberId, method, uri)
                val value = "1"
                val timeout = CacheKey.REQUEST_LOCK.timeOut

                every { keyValueCache.setIfAbsent(key = key, value = value, timeout = timeout) } returns true

                requestLockService.validate(memberId = memberId, method = method, uri = uri)

                verify(exactly = 1) { keyValueCache.setIfAbsent(key = key, value = value, timeout = timeout) }
            }
        }

        context("RequestLock 있을 경우") {
            it("DuplicateRequestException 발생한다") {
                val memberId = 1L
                val method = "GET"
                val uri = "/members"

                val key = CacheKey.REQUEST_LOCK.generate(memberId, method, uri)
                val value = "1"
                val timeout = CacheKey.REQUEST_LOCK.timeOut

                every { keyValueCache.setIfAbsent(key = key, value = value, timeout = timeout) } returns false

                shouldThrow<DuplicateRequestException> {
                    requestLockService.validate(
                        memberId = memberId,
                        method = method,
                        uri = uri
                    )
                }
            }
        }
    }
})
