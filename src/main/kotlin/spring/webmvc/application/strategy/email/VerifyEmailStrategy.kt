package spring.webmvc.application.strategy.email

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Component
import spring.webmvc.application.dto.command.VerifyEmailCommand
import spring.webmvc.application.enums.EmailTemplate
import spring.webmvc.infrastructure.external.smtp.EmailSender

@Component
class VerifyEmailStrategy(
    private val objectMapper: ObjectMapper,
    private val emailSender: EmailSender,
) : EmailStrategy {
    override fun emailTemplate() = EmailTemplate.JOIN_VERIFY

    override fun handle(payload: String) {
        val command = objectMapper.readValue<VerifyEmailCommand>(payload)

        emailSender.send(
            to = command.email,
            subject = emailTemplate().subject,
            templatePath = emailTemplate().templatePath,
            variables = mapOf("verifyLink" to command.verifyLink),
        )
    }
}
