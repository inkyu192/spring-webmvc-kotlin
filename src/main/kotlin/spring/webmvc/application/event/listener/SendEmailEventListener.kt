package spring.webmvc.application.event.listener

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener
import spring.webmvc.application.event.SendPasswordResetEmailEvent
import spring.webmvc.application.event.SendVerifyEmailEvent
import spring.webmvc.domain.repository.cache.AuthCacheRepository
import spring.webmvc.infrastructure.external.notification.NotificationClient
import spring.webmvc.infrastructure.external.notification.PasswordResetEmailRequest
import spring.webmvc.infrastructure.external.notification.VerifyEmailRequest
import java.util.*

@Component
class SendEmailEventListener(
    private val notificationClient: NotificationClient,
    private val authCacheRepository: AuthCacheRepository,
) {
    @Async
    @TransactionalEventListener
    fun sendVerifyEmail(event: SendVerifyEmailEvent) {
        val token = UUID.randomUUID().toString()
        val verifyLink = "http://localhost:8080/auth/join/verify?token=$token"

        authCacheRepository.setJoinVerifyToken(token = token, email = event.email)

        notificationClient.sendVerifyEmail(
            VerifyEmailRequest(
                email = event.email.value,
                verifyLink = verifyLink,
            )
        )
    }

    @Async
    @TransactionalEventListener
    fun sendPasswordResetEmail(event: SendPasswordResetEmailEvent) {
        val token = UUID.randomUUID().toString()
        val resetLink = "http://localhost:8080/auth/password/reset?token=$token"

        authCacheRepository.setPasswordResetToken(token = token, email = event.email)

        notificationClient.sendPasswordResetEmail(
            PasswordResetEmailRequest(
                email = event.email.value,
                resetLink = resetLink,
            )
        )
    }
}
