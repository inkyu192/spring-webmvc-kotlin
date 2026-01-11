package spring.webmvc.presentation.controller.partner

import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.dto.query.UserQuery
import spring.webmvc.application.service.UserService
import spring.webmvc.presentation.dto.response.UserDetailResponse
import spring.webmvc.presentation.dto.response.UserPageResponse
import java.time.Instant

@RestController("partnerUserController")
@RequestMapping("/partner/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    fun findUsers(
        @PageableDefault pageable: Pageable,
        @RequestParam(required = false) phone: String?,
        @RequestParam(required = false) name: String?,
        @RequestParam createdFrom: Instant,
        @RequestParam createdTo: Instant,
    ): UserPageResponse {
        val query = UserQuery.create(
            pageable = pageable,
            phone = phone,
            name = name,
            createdFrom = createdFrom,
            createdTo = createdTo
        )

        val page = userService.findUsers(query)

        return UserPageResponse.from(page)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    fun findUser(@PathVariable id: Long): UserDetailResponse {
        val userDetail = userService.findUserDetail(id)

        return UserDetailResponse.from(
            user = userDetail.user,
            credential = userDetail.credential,
            oauths = userDetail.oauths,
        )
    }
}
