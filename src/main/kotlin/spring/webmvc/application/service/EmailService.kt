package spring.webmvc.application.service

import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import spring.webmvc.application.enums.EmailTemplate
import spring.webmvc.application.event.SendPasswordResetEmailEvent
import spring.webmvc.application.event.SendVerifyEmailEvent
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.repository.cache.AuthCacheRepository
import java.util.*

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    private val templateEngine: TemplateEngine,
    private val authCacheRepository: AuthCacheRepository,
) {
    fun sendVerifyEmail(event: SendVerifyEmailEvent) {
        val token = UUID.randomUUID().toString()

        authCacheRepository.setJoinVerifyToken(
            token = token,
            email = event.email,
        )

        val verifyLink = "http://localhost:8080/auth/join/verify?token=$token"

        sendEmail(
            to = event.email,
            template = EmailTemplate.JOIN_VERIFY,
            variables = mapOf(
                "verifyLink" to verifyLink,
            ),
        )
    }

    fun sendPasswordResetEmail(event: SendPasswordResetEmailEvent) {
        val token = UUID.randomUUID().toString()

        authCacheRepository.setPasswordResetToken(
            token = token,
            email = event.email,
        )

        val resetLink = "http://localhost:8080/auth/password/reset?token=$token"

        sendEmail(
            to = event.email,
            template = EmailTemplate.PASSWORD_RESET,
            variables = mapOf(
                "resetLink" to resetLink,
            ),
        )
    }

    private fun sendEmail(
        to: Email,
        template: EmailTemplate,
        variables: Map<String, Any> = emptyMap(),
    ) {
        val htmlContent = templateEngine.process(
            template.templatePath,
            Context().apply { setVariables(variables) }
        )

        val mimeMessage = mailSender.createMimeMessage().apply {
            MimeMessageHelper(this, true, "UTF-8").apply {
                setTo(to.value)
                setSubject(template.subject)
                setText(htmlContent, true)
            }
        }

        mailSender.send(mimeMessage)
    }
}
