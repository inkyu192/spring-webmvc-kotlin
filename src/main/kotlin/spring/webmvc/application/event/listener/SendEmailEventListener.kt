package spring.webmvc.application.event.listener

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener
import spring.webmvc.application.event.SendPasswordResetEmailEvent
import spring.webmvc.application.event.SendVerifyEmailEvent
import spring.webmvc.application.service.EmailService

@Component
class SendEmailEventListener(
    private val emailService: EmailService,
) {
    @Async
    @TransactionalEventListener
    fun sendVerifyEmail(event: SendVerifyEmailEvent) {
        emailService.sendVerifyEmail(event)
    }

    @Async
    @TransactionalEventListener
    fun sendPasswordResetEmail(event: SendPasswordResetEmailEvent) {
        emailService.sendPasswordResetEmail(event)
    }
}