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
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.domain.repository.PermissionRepository
import spring.webmvc.domain.repository.RoleRepository
import spring.webmvc.presentation.exception.EntityNotFoundException
import java.time.Instant
import java.time.LocalDate

class MemberServiceTest : DescribeSpec({
    val memberRepository = mockk<MemberRepository>()
    val roleRepository = mockk<RoleRepository>()
    val permissionRepository = mockk<PermissionRepository>()
    val passwordEncoder = mockk<PasswordEncoder>()
    val eventPublisher = mockk<ApplicationEventPublisher>()
    val memberService = MemberService(
        memberRepository = memberRepository,
        roleRepository = roleRepository,
        permissionRepository = permissionRepository,
        passwordEncoder = passwordEncoder,
        eventPublisher = eventPublisher
    )

    beforeTest {
        val authentication = UsernamePasswordAuthenticationToken(
            1L,
            null,
            listOf(SimpleGrantedAuthority("ROLE_USER"))
        )

        SecurityContextHolder.getContext().authentication = authentication
    }

    afterTest {
        SecurityContextHolder.clearContext()
    }

    describe("findMember 는") {
        context("엔티티가 존재하지 않을 경우") {
            it("EntityNotFoundException 던진다") {
                every { memberRepository.findByIdOrNull(any()) } returns null

                shouldThrow<EntityNotFoundException> { memberService.findMember() }
            }
        }

        context("엔티티가 존재할 경우") {
            it("MemberResponse 반환한다") {
                val memberId = 1L
                val member = mockk<Member> {
                    every { id } returns memberId
                    every { account } returns "test@gmail.com"
                    every { name } returns "name"
                    every { phone } returns "010-1234-1234"
                    every { birthDate } returns LocalDate.now()
                    every { createdAt } returns Instant.now()
                }

                every { memberRepository.findByIdOrNull(any()) } returns member

                memberService.findMember().apply {
                    this.id shouldBe memberId
                }
            }
        }
    }
})
