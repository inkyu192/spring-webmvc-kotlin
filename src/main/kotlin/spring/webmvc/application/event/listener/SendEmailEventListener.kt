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
import spring.webmvc.infrastructure.properties.AppProperties
import java.util.*

@Component
class SendEmailEventListener(
    private val appProperties: AppProperties,
    private val notificationClient: NotificationClient,
    private val authCacheRepository: AuthCacheRepository,
) {
    @Async
    @TransactionalEventListener
    fun sendVerifyEmail(event: SendVerifyEmailEvent) {
        val token = UUID.randomUUID().toString()
        val verifyLink = "${appProperties.baseUrl}/auth/join/verify?token=$token"

        authCacheRepository.setJoinVerifyToken(token = token, email = event.email)

        notificationClient.sendVerifyEmail(
            request = VerifyEmailRequest(
                email = event.email.value,
                verifyLink = verifyLink,
            )
        )
    }

    @Async
    @TransactionalEventListener
    fun sendPasswordResetEmail(event: SendPasswordResetEmailEvent) {
        val token = UUID.randomUUID().toString()
        val resetLink = "${appProperties.baseUrl}/auth/password/reset?token=$token"

        authCacheRepository.setPasswordResetToken(token = token, email = event.email)

        notificationClient.sendPasswordResetEmail(
            request = PasswordResetEmailRequest(
                email = event.email.value,
                resetLink = resetLink,
            )
        )
    }
}
