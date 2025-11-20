package spring.webmvc.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.domain.repository.RoleRepository
import spring.webmvc.presentation.exception.EntityNotFoundException
import java.time.Instant
import java.time.LocalDate

class MemberServiceTest : DescribeSpec({
    val memberRepository = mockk<MemberRepository>()
    val roleRepository = mockk<RoleRepository>()
    val permissionService = mockk<PermissionService>()
    val passwordEncoder = mockk<PasswordEncoder>()
    val eventPublisher = mockk<ApplicationEventPublisher>()
    val memberService = MemberService(
        memberRepository = memberRepository,
        roleRepository = roleRepository,
        permissionService = permissionService,
        passwordEncoder = passwordEncoder,
        eventPublisher = eventPublisher
    )

    beforeTest {
        val authentication = UsernamePasswordAuthenticationToken(
            1L,
            null,
            listOf(SimpleGrantedAuthority("TEST"))
        )

        SecurityContextHolder.getContext().authentication = authentication
    }

    afterTest {
        SecurityContextHolder.clearContext()
    }

    describe("findMember") {
        context("Member 없을 경우") {
            it("EntityNotFoundException 발생한다") {
                val memberId = 1L

                every { memberRepository.findById(memberId) } throws EntityNotFoundException(Member::class, memberId)

                shouldThrow<EntityNotFoundException> { memberService.findMember() }
            }
        }

        context("Member 있을 경우") {
            it("조회 후 반환한다") {
                val memberId = 1L
                val member = mockk<Member> {
                    every { id } returns memberId
                    every { email } returns Email.create("test@gmail.com")
                    every { name } returns "name"
                    every { phone } returns Phone.create("010-1234-1234")
                    every { birthDate } returns LocalDate.now()
                    every { createdAt } returns Instant.now()
                }

                every { memberRepository.findById(any()) } returns member

                val result = memberService.findMember()

                result.id shouldBe memberId
                result.email shouldBe member.email
                result.name shouldBe member.name
                result.phone shouldBe member.phone
                result.birthDate shouldBe member.birthDate
                result.createdAt shouldBe member.createdAt
            }
        }
    }
})
