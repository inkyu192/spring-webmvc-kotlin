package spring.webmvc.presentation.controller

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import spring.webmvc.application.service.AuthService
import spring.webmvc.presentation.dto.request.MemberLoginRequest
import spring.webmvc.presentation.dto.request.TokenRequest
import spring.webmvc.presentation.dto.response.TokenResponse

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/login")
    fun login(@RequestBody @Validated memberLoginRequest: MemberLoginRequest) =
        authService.login(account = memberLoginRequest.account, password = memberLoginRequest.password)
            .let { TokenResponse(accessToken = it.first, refreshToken = it.second) }

    @PostMapping("/token/refresh")
    fun refreshToken(@RequestBody @Validated tokenRequest: TokenRequest) =
        authService.refreshToken(accessToken = tokenRequest.accessToken, refreshToken = tokenRequest.refreshToken)
            .let { TokenResponse(accessToken = it.first, refreshToken = it.second) }
}