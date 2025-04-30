package spring.webmvc.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import spring.webmvc.domain.repository.RequestLockRepository
import spring.webmvc.presentation.exception.DuplicateRequestException

class RequestLockServiceTest : DescribeSpec({
    val requestLockRepository = mockk<RequestLockRepository>()
    val requestLockService = RequestLockService(requestLockRepository)

    describe("validate") {
        context("RequestLock 없을 경우") {
            it("저장한다") {
                val memberId = 1L
                val method = "GET"
                val uri = "/members"

                every {
                    requestLockRepository.setIfAbsent(
                        memberId = memberId,
                        method = method,
                        uri = uri
                    )
                } returns true

                requestLockService.validate(memberId = memberId, method = method, uri = uri)

                verify(exactly = 1) {
                    requestLockRepository.setIfAbsent(
                        memberId = memberId,
                        method = method,
                        uri = uri
                    )
                }
            }
        }

        context("RequestLock 있을 경우") {
            it("DuplicateRequestException 발생한다") {
                val memberId = 1L
                val method = "GET"
                val uri = "/members"

                every {
                    requestLockRepository.setIfAbsent(
                        memberId = memberId,
                        method = method,
                        uri = uri
                    )
                } returns false

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
