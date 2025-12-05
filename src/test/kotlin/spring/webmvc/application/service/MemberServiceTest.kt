package spring.webmvc.application.service

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import spring.webmvc.application.dto.query.MemberSearchQuery
import spring.webmvc.application.event.SendVerifyEmailEvent
import spring.webmvc.domain.dto.command.MemberCreateCommand
import spring.webmvc.domain.dto.command.MemberStatusUpdateCommand
import spring.webmvc.domain.dto.command.MemberUpdateCommand
import spring.webmvc.domain.dto.command.PasswordChangeCommand
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.enums.MemberStatus
import spring.webmvc.domain.model.enums.MemberType
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.domain.repository.PermissionRepository
import spring.webmvc.domain.repository.RoleRepository
import spring.webmvc.domain.service.MemberDomainService
import spring.webmvc.presentation.exception.DuplicateEntityException
import java.time.Instant
import java.time.LocalDate

class MemberServiceTest {
    private val memberRepository = mockk<MemberRepository>()
    private val roleRepository = mockk<RoleRepository>()
    private val permissionRepository = mockk<PermissionRepository>()
    private val memberDomainService = mockk<MemberDomainService>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val eventPublisher = mockk<ApplicationEventPublisher>()
    private val memberService = MemberService(
        memberRepository = memberRepository,
        roleRepository = roleRepository,
        permissionRepository = permissionRepository,
        memberDomainService = memberDomainService,
        passwordEncoder = passwordEncoder,
        eventPublisher = eventPublisher,
    )

    private lateinit var member: Member
    private lateinit var email: Email
    private lateinit var phone: Phone
    private lateinit var command: MemberCreateCommand
    private lateinit var roleIds: List<Long>
    private lateinit var permissionIds: List<Long>

    @BeforeEach
    fun setUp() {
        email = Email.create("test@example.com")
        phone = Phone.create("010-1234-5678")
        roleIds = listOf(1L)
        permissionIds = listOf(1L)
        member = Member.create(
            email = email,
            password = "encodedPassword",
            name = "홍길동",
            phone = phone,
            birthDate = LocalDate.of(1990, 1, 1),
            type = MemberType.CUSTOMER,
        )
        command = MemberCreateCommand(
            email = email,
            password = "password123",
            name = "홍길동",
            phone = phone,
            birthDate = LocalDate.of(1990, 1, 1),
            memberType = MemberType.CUSTOMER,
            roleIds = roleIds,
            permissionIds = permissionIds,
        )
    }

    @Test
    @DisplayName("회원 생성 성공")
    fun createMember() {
        every { memberRepository.existsByEmail(email) } returns false
        every { roleRepository.findAllById(roleIds) } returns emptyList()
        every { permissionRepository.findAllById(permissionIds) } returns emptyList()
        every { passwordEncoder.encode(command.password) } returns "encodedPassword"
        every {
            memberDomainService.createMember(
                command.copy(password = "encodedPassword"),
                emptyList(),
                emptyList()
            )
        } returns member
        every { memberRepository.save(member) } returns member
        every { eventPublisher.publishEvent(SendVerifyEmailEvent(email)) } just runs

        val result = memberService.createMember(command)

        Assertions.assertThat(result).isEqualTo(member)
    }

    @Test
    @DisplayName("중복된 이메일로 회원 생성 시 DuplicateEntityException 발생")
    fun createMemberWithDuplicateEmail() {
        every { memberRepository.existsByEmail(email) } returns true

        Assertions.assertThatThrownBy { memberService.createMember(command) }
            .isInstanceOf(DuplicateEntityException::class.java)
    }

    @Test
    @DisplayName("회원 목록 조회")
    fun findMembers() {
        val pageable = PageRequest.of(0, 10)
        val createdFrom = Instant.now()
        val createdTo = Instant.now()
        val query = MemberSearchQuery(
            pageable = pageable,
            email = email,
            phone = null,
            name = null,
            status = null,
            createdFrom = createdFrom,
            createdTo = createdTo,
        )
        val page = PageImpl(listOf(member))

        every { memberRepository.findAll(pageable, email, null, null, null, createdFrom, createdTo) } returns page

        val result = memberService.findMembers(query)

        Assertions.assertThat(result).isEqualTo(page)
    }

    @Test
    @DisplayName("회원 단건 조회")
    fun findMember() {
        val memberId = 1L

        every { memberRepository.findById(memberId) } returns member

        val result = memberService.findMember(memberId)

        Assertions.assertThat(result).isEqualTo(member)
    }

    @Test
    @DisplayName("회원 정보 수정")
    fun updateMember() {
        val command = MemberUpdateCommand(
            memberId = 1L,
            name = "김철수",
            phone = Phone.create("010-9876-5432"),
            birthDate = LocalDate.of(1995, 5, 15),
        )

        every { memberRepository.findById(command.memberId) } returns member

        val result = memberService.updateMember(command)

        Assertions.assertThat(result).isEqualTo(member)
        Assertions.assertThat(result.name).isEqualTo("김철수")
    }

    @Test
    @DisplayName("회원 상태 수정")
    fun updateMemberStatus() {
        val command = MemberStatusUpdateCommand(
            memberId = 1L,
            status = MemberStatus.SUSPENDED,
        )

        every { memberRepository.findById(command.memberId) } returns member

        val result = memberService.updateMemberStatus(command)

        Assertions.assertThat(result).isEqualTo(member)
        Assertions.assertThat(result.status).isEqualTo(MemberStatus.SUSPENDED)
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    fun updatePassword() {
        val memberId = 1L
        val oldPassword = "oldPassword"
        val newPassword = "newPassword"
        val encodedNewPassword = "encodedNewPassword"
        val passwordChangeCommand = PasswordChangeCommand(
            memberId = memberId,
            oldPassword = oldPassword,
            newPassword = newPassword,
        )

        every { memberRepository.findById(memberId) } returns member
        every { passwordEncoder.matches(oldPassword, member.password) } returns true
        every { passwordEncoder.encode(newPassword) } returns encodedNewPassword

        memberService.updatePassword(passwordChangeCommand)

        Assertions.assertThat(member.password).isEqualTo(encodedNewPassword)
    }

    @Test
    @DisplayName("잘못된 기존 비밀번호로 변경 시 BadCredentialsException 발생")
    fun updatePasswordWithWrongOldPassword() {
        val memberId = 1L
        val wrongPassword = "wrongPassword"
        val newPassword = "newPassword"
        val passwordChangeCommand = PasswordChangeCommand(
            memberId = memberId,
            oldPassword = wrongPassword,
            newPassword = newPassword,
        )

        every { memberRepository.findById(memberId) } returns member
        every { passwordEncoder.matches(wrongPassword, member.password) } returns false

        Assertions.assertThatThrownBy { memberService.updatePassword(passwordChangeCommand) }
            .isInstanceOf(BadCredentialsException::class.java)
    }
}
