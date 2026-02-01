package spring.webmvc.presentation.controller

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.AuthService
import spring.webmvc.infrastructure.properties.AppProperties
import spring.webmvc.presentation.dto.request.*
import spring.webmvc.presentation.dto.response.SignUpResponse
import spring.webmvc.presentation.dto.response.TokenResponse

@Controller
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val appProperties: AppProperties,
) {
    @PostMapping("/sign-up")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    fun signUp(
        @RequestBody @Validated request: SignUpRequest,
    ): SignUpResponse {
        val command = request.toCommand()
        val user = authService.signUp(command)

        return SignUpResponse.of(user, appProperties.aws.cloudfront.domain)
    }

    @PostMapping("/sign-in")
    @ResponseBody
    fun signIn(
        @RequestBody @Validated request: SignInRequest,
    ): TokenResponse {
        val command = request.toCommand()
        val tokenResult = authService.signIn(command)

        return TokenResponse.of(tokenResult)
    }

    @PostMapping("/token/refresh")
    @ResponseBody
    fun refreshToken(
        @RequestBody @Validated request: TokenRequest,
    ): TokenResponse {
        val command = request.toCommand()
        val tokenResult = authService.refreshToken(command)

        return TokenResponse.of(tokenResult)
    }

    @PostMapping("/join/verify/request")
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun requestJoinVerify(
        @RequestBody @Validated request: JoinVerifyRequest,
    ) {
        val command = request.toCommand()
        authService.requestJoinVerify(command)
    }

    @GetMapping("/join/verify")
    fun getJoinVerifyForm(
        @RequestParam token: String,
        model: Model,
    ): String {
        model.addAttribute("token", token)
        return "email/join-verify-form"
    }

    @PostMapping("/join/verify/confirm")
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun confirmJoinVerify(
        @RequestBody @Validated request: JoinVerifyConfirmRequest,
    ) {
        val command = request.toCommand()
        authService.confirmJoinVerify(command)
    }

    @GetMapping("/join/verify/success")
    fun joinVerifySuccess() = "email/join-verify-success"

    @PostMapping("/password/reset/request")
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun requestPasswordReset(
        @RequestBody @Validated request: PasswordResetRequest,
    ) {
        val command = request.toCommand()
        authService.requestPasswordReset(command)
    }

    @GetMapping("/password/reset")
    fun getPasswordResetForm(
        @RequestParam token: String,
        model: Model,
    ): String {
        model.addAttribute("token", token)
        return "email/password-reset-form"
    }

    @PostMapping("/password/reset/confirm")
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun confirmPasswordReset(
        @RequestBody @Validated request: PasswordResetConfirmRequest,
    ) {
        val command = request.toCommand()
        authService.confirmPasswordReset(command)
    }

    @GetMapping("/password/reset/success")
    fun passwordResetSuccess() = "email/password-reset-success"
}
