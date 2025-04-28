package spring.webmvc.presentation.controller

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.MemberService
import spring.webmvc.presentation.dto.request.MemberCreateRequest
import spring.webmvc.presentation.dto.request.MemberUpdateRequest
import spring.webmvc.presentation.dto.response.MemberResponse
import spring.webmvc.presentation.exception.AtLeastOneRequiredException

@RestController
@RequestMapping("members")
class MemberController(
    private val memberService: MemberService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createMember(@RequestBody @Validated memberCreateRequest: MemberCreateRequest): MemberResponse {
        if (memberCreateRequest.roleIds.isEmpty() && memberCreateRequest.permissionIds.isEmpty()) {
            throw AtLeastOneRequiredException("roleIds", "permissionIds")
        }

        return MemberResponse(
            member = memberService.createMember(
                account = memberCreateRequest.account,
                password = memberCreateRequest.password,
                name = memberCreateRequest.name,
                phone = memberCreateRequest.phone,
                birthDate = memberCreateRequest.birthDate,
                roleIds = memberCreateRequest.roleIds,
                permissionIds = memberCreateRequest.permissionIds,
            )
        )
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    fun findMember() = MemberResponse(member = memberService.findMember())

    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    fun updateMember(@RequestBody @Validated memberUpdateRequest: MemberUpdateRequest) =
        MemberResponse(
            member = memberService.updateMember(
                password = memberUpdateRequest.password,
                name = memberUpdateRequest.name,
                phone = memberUpdateRequest.phone,
                birthDate = memberUpdateRequest.birthDate,
            )
        )

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMember() {
        memberService.deleteMember()
    }
}
