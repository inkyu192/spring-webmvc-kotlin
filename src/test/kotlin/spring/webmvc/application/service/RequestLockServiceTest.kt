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

    describe("validate 는") {
        val memberId = 1L
        val method = "GET"
        val uri = "/members"

        context("데이터가 없을 경우") {
            it("저장한다") {
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

        context("데이터가 있을 경우") {
            it("DuplicateRequestException 던진다") {
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
