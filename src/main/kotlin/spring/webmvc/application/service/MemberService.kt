package spring.webmvc.application.service

import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.query.MemberSearchQuery
import spring.webmvc.application.event.SendVerifyEmailEvent
import spring.webmvc.domain.dto.command.MemberCreateCommand
import spring.webmvc.domain.dto.command.MemberStatusUpdateCommand
import spring.webmvc.domain.dto.command.MemberUpdateCommand
import spring.webmvc.domain.dto.command.PasswordChangeCommand
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.domain.repository.PermissionRepository
import spring.webmvc.domain.repository.RoleRepository
import spring.webmvc.domain.service.MemberDomainService
import spring.webmvc.presentation.exception.DuplicateEntityException

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository,
    private val memberDomainService: MemberDomainService,
    private val passwordEncoder: PasswordEncoder,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional
    fun createMember(command: MemberCreateCommand): Member {
        if (memberRepository.existsByEmail(command.email)) {
            throw DuplicateEntityException(kClass = Member::class, name = command.email.value)
        }

        val roles = roleRepository.findAllById(command.roleIds)
        val permissions = permissionRepository.findAllById(command.permissionIds)

        val member = memberDomainService.createMember(
            command = command.copy(
                password = passwordEncoder.encode(command.password),
            ),
            roles = roles,
            permissions = permissions,
        )

        memberRepository.save(member)

        eventPublisher.publishEvent(SendVerifyEmailEvent(email = command.email))

        return member
    }

    fun findMembers(query: MemberSearchQuery) = memberRepository.findAll(
        pageable = query.pageable,
        email = query.email,
        phone = query.phone,
        name = query.name,
        status = query.status,
        createdFrom = query.createdFrom,
        createdTo = query.createdTo,
    )

    fun findMember(memberId: Long) = memberRepository.findById(memberId)

    @Transactional
    fun updateMember(command: MemberUpdateCommand): Member {
        val member = memberRepository.findById(command.memberId)

        member.update(
            name = command.name,
            phone = command.phone,
            birthDate = command.birthDate,
        )

        return member
    }

    @Transactional
    fun updateMemberStatus(command: MemberStatusUpdateCommand): Member {
        val member = memberRepository.findById(command.memberId)

        member.updateStatus(command.status)

        return member
    }

    @Transactional
    fun updatePassword(command: PasswordChangeCommand) {
        val member = memberRepository.findById(command.memberId)

        if (!passwordEncoder.matches(command.oldPassword, member.password)) {
            throw BadCredentialsException("유효하지 않은 인증 정보입니다.")
        }

        member.updatePassword(passwordEncoder.encode(command.newPassword))
    }
}
