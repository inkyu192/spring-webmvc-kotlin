package spring.webmvc.presentation.controller

import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.EmailService
import spring.webmvc.presentation.dto.request.PasswordResetEmailRequest
import spring.webmvc.presentation.dto.request.VerifyEmailRequest

@RestController
@RequestMapping("/email")
class EmailController(
    private val emailService: EmailService,
) {
    @PostMapping("/verify")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun sendVerifyEmail(@RequestBody @Validated request: VerifyEmailRequest) {
        val command = request.toCommand()

        emailService.sendVerifyEmail(command)
    }

    @PostMapping("/password-reset")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun sendPasswordResetEmail(@RequestBody @Validated request: PasswordResetEmailRequest) {
        val command = request.toCommand()

        emailService.sendPasswordResetEmail(command)
    }
}
