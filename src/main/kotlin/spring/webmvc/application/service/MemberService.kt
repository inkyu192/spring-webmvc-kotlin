package spring.webmvc.application.service

import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.event.NotificationEvent
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.entity.Role
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.domain.repository.RoleRepository
import spring.webmvc.infrastructure.util.SecurityContextUtil
import spring.webmvc.presentation.dto.request.MemberSaveRequest
import spring.webmvc.presentation.dto.request.MemberUpdateRequest
import spring.webmvc.presentation.dto.response.MemberResponse
import spring.webmvc.presentation.exception.DuplicateEntityException
import spring.webmvc.presentation.exception.EntityNotFoundException

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
    fun saveMember(memberSaveRequest: MemberSaveRequest): MemberResponse {
        if (memberRepository.existsByAccount(memberSaveRequest.account)) {
            throw DuplicateEntityException(clazz = Member::class.java, name = memberSaveRequest.account)
        }

        val member = Member.create(
            account = memberSaveRequest.account,
            password = passwordEncoder.encode(memberSaveRequest.password),
            name = memberSaveRequest.name,
            phone = memberSaveRequest.phone,
            birthDate = memberSaveRequest.birthDate,
        )

        val roleMap = roleRepository.findAllById(memberSaveRequest.roleIds).associateBy { it.id }
        memberSaveRequest.roleIds.forEach {
            val role = roleMap[it] ?: throw EntityNotFoundException(clazz = Role::class.java, id = it)
            member.addRole(role)
        }

        permissionService.addPermission(
            permissionIds = memberSaveRequest.permissionIds,
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

        return MemberResponse(member)
    }

    fun findMember(): MemberResponse {
        val memberId = SecurityContextUtil.getMemberId()
        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw EntityNotFoundException(clazz = Member::class.java, id = memberId)

        return MemberResponse(member)
    }

    @Transactional
    fun updateMember(memberUpdateRequest: MemberUpdateRequest): MemberResponse {
        val memberId = SecurityContextUtil.getMemberId()
        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw EntityNotFoundException(clazz = Member::class.java, id = memberId)

        member.update(
            password = passwordEncoder.encode(memberUpdateRequest.password),
            name = memberUpdateRequest.name,
            phone = memberUpdateRequest.phone,
            birthDate = memberUpdateRequest.birthDate,
        )

        return MemberResponse(member)
    }

    @Transactional
    fun deleteMember() {
        val memberId = SecurityContextUtil.getMemberId()
        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw EntityNotFoundException(clazz = Member::class.java, id = memberId)

        memberRepository.delete(member)
    }
}
