package spring.webmvc.presentation.controller

import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.dto.query.UserSearchQuery
import spring.webmvc.application.service.UserService
import spring.webmvc.application.dto.command.UserStatusUpdateCommand
import spring.webmvc.domain.model.enums.UserStatus
import spring.webmvc.presentation.dto.request.UserUpdateRequest
import spring.webmvc.presentation.dto.response.UserPageResponse
import spring.webmvc.presentation.dto.response.UserResponse
import java.time.Instant

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    fun findUsers(
        @PageableDefault pageable: Pageable,
        @RequestParam(required = false) email: String?,
        @RequestParam(required = false) phone: String?,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) status: UserStatus?,
        @RequestParam createdFrom: Instant,
        @RequestParam createdTo: Instant,
    ): UserPageResponse {
        val query = UserSearchQuery.create(
            pageable = pageable,
            email = email,
            phone = phone,
            name = name,
            status = status,
            createdFrom = createdFrom,
            createdTo = createdTo
        )
        val page = userService.findUsers(query)

        return UserPageResponse.from(page)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    fun findUser(@PathVariable id: Long): UserResponse {
        val user = userService.findUser(id)

        return UserResponse.from(user)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    fun updateUser(
        @PathVariable id: Long,
        @RequestBody @Validated request: UserUpdateRequest,
    ): UserResponse {
        val command = request.toCommand(id)
        val user = userService.updateUser(command)

        return UserResponse.from(user)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('USER_WRITE')")
    fun deleteUser(
        @PathVariable id: Long,
    ) {
        val command = UserStatusUpdateCommand(id = id, status = UserStatus.WITHDRAWN)

        userService.updateUserStatus(command)
    }
}