package spring.webmvc.application.service

import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.event.NotificationEvent
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.entity.Role
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.domain.repository.RoleRepository
import spring.webmvc.infrastructure.security.SecurityContextUtil
import spring.webmvc.presentation.exception.DuplicateEntityException
import spring.webmvc.presentation.exception.EntityNotFoundException
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,
    private val roleRepository: RoleRepository,
    private val permissionService: PermissionService,
    private val passwordEncoder: PasswordEncoder,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional
    fun createMember(
        email: String,
        password: String,
        name: String,
        phone: String,
        birthDate: LocalDate,
        roleIds: List<Long>,
        permissionIds: List<Long>,
    ): Member {
        if (memberRepository.existsByEmail(Email.create(email))) {
            throw DuplicateEntityException(kClass = Member::class, name = email)
        }

        val member = Member.create(
            email = email,
            password = passwordEncoder.encode(password),
            name = name,
            phone = phone,
            birthDate = birthDate,
        )

        val roleMap = roleRepository.findAllById(roleIds).associateBy { it.id }
        roleIds.forEach {
            val role = roleMap[it] ?: throw EntityNotFoundException(kClass = Role::class, id = it)
            member.addRole(role)
        }

        permissionService.addPermission(
            permissionIds = permissionIds,
            consumer = member::addPermission
        )

        memberRepository.save(member)

        eventPublisher.publishEvent(
            NotificationEvent(
                memberId = checkNotNull(member.id),
                title = "회원가입 완료",
                message = "회원가입을 환영합니다!",
                url = "/test/123"
            )
        )

        return member
    }

    fun findMember(): Member {
        val memberId = SecurityContextUtil.getMemberId()

        return memberRepository.findByIdOrNull(memberId)
            ?: throw EntityNotFoundException(kClass = Member::class, id = memberId)
    }

    @Transactional
    fun updateMember(
        password: String?,
        name: String?,
        phone: String?,
        birthDate: LocalDate?,
    ): Member {
        val memberId = SecurityContextUtil.getMemberId()
        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw EntityNotFoundException(kClass = Member::class, id = memberId)

        member.update(
            password = passwordEncoder.encode(password),
            name = name,
            phone = phone,
            birthDate = birthDate,
        )

        return member
    }

    @Transactional
    fun deleteMember() {
        val memberId = SecurityContextUtil.getMemberId()
        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw EntityNotFoundException(kClass = Member::class, id = memberId)

        memberRepository.delete(member)
    }
}
