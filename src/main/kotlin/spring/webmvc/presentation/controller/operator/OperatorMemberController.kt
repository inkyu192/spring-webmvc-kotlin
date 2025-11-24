package spring.webmvc.presentation.controller.operator

import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.MemberService
import spring.webmvc.presentation.dto.request.MemberSearchRequest
import spring.webmvc.presentation.dto.request.MemberStatusUpdateRequest
import spring.webmvc.presentation.dto.response.MemberPageResponse
import spring.webmvc.presentation.dto.response.MemberResponse

@RestController
@RequestMapping("/operator/members")
class OperatorMemberController(
    private val memberService: MemberService,
) {
    @GetMapping
    @PreAuthorize("hasAuthority('OPERATOR_MEMBER_READ')")
    fun findMembers(
        @PageableDefault pageable: Pageable,
        @ModelAttribute request: MemberSearchRequest,
    ): MemberPageResponse {
        val query = request.toQuery(pageable)
        val page = memberService.findMembers(query)

        return MemberPageResponse.from(page)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('OPERATOR_MEMBER_READ')")
    fun findMember(@PathVariable id: Long): MemberResponse {
        val member = memberService.findMember(id)

        return MemberResponse.from(member)
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('OPERATOR_MEMBER_WRITE')")
    fun updateMemberStatus(
        @PathVariable id: Long,
        @RequestBody @Validated request: MemberStatusUpdateRequest,
    ): MemberResponse {
        val member = memberService.updateMemberStatus(request.toCommand(id))

        return MemberResponse.from(member)
    }
}