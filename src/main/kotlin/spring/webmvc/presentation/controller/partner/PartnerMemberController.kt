package spring.webmvc.presentation.controller.partner

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.MemberService
import spring.webmvc.domain.dto.command.MemberStatusUpdateCommand
import spring.webmvc.domain.model.enums.MemberStatus
import spring.webmvc.domain.model.enums.MemberType
import spring.webmvc.infrastructure.security.SecurityContextUtil
import spring.webmvc.presentation.dto.request.MemberCreateRequest
import spring.webmvc.presentation.dto.request.MemberUpdateRequest
import spring.webmvc.presentation.dto.request.PasswordChangeRequest
import spring.webmvc.presentation.dto.response.MemberResponse

@RestController
@RequestMapping("/partner/members")
class PartnerMemberController(
    private val memberService: MemberService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createMember(
        @RequestBody @Validated request: MemberCreateRequest,
    ): MemberResponse {
        val command = request.toCommand(MemberType.PARTNER)
        val member = memberService.createMember(command)

        return MemberResponse.from(member)
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    fun findMember(): MemberResponse {
        val memberId = SecurityContextUtil.getMemberId()
        val member = memberService.findMember(memberId)

        return MemberResponse.from(member)
    }

    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    fun updateMember(
        @RequestBody @Validated request: MemberUpdateRequest,
    ): MemberResponse {
        val memberId = SecurityContextUtil.getMemberId()
        val member = memberService.updateMember(request.toCommand(memberId))

        return MemberResponse.from(member)
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    fun deleteMember() {
        val memberId = SecurityContextUtil.getMemberId()
        val command = MemberStatusUpdateCommand(memberId = memberId, status = MemberStatus.WITHDRAWN)

        memberService.updateMemberStatus(command)
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    fun updatePassword(
        @RequestBody @Validated request: PasswordChangeRequest,
    ) {
        val memberId = SecurityContextUtil.getMemberId()

        memberService.updatePassword(request.toCommand(memberId))
    }
}