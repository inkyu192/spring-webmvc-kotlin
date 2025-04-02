package spring.webmvc.presentation.controller

import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.RoleService
import spring.webmvc.presentation.dto.request.RoleSaveRequest

@RestController
@RequestMapping("/roles")
class RoleController(
    private val roleService: RoleService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun saveRole(@RequestBody @Validated roleSaveRequest: RoleSaveRequest) = roleService.saveRole(roleSaveRequest)
}