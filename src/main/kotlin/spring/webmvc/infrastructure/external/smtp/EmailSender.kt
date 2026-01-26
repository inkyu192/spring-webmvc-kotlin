package spring.webmvc.infrastructure.external.smtp

import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Component
class EmailSender(
    private val mailSender: JavaMailSender,
    private val templateEngine: TemplateEngine,
) {
    fun send(
        to: String,
        subject: String,
        templatePath: String,
        variables: Map<String, Any>,
    ) {
        val htmlContent = templateEngine.process(
            templatePath,
            Context().apply { setVariables(variables) },
        )

        val mimeMessage = mailSender.createMimeMessage().apply {
            MimeMessageHelper(this, true, "UTF-8").apply {
                setTo(to)
                setSubject(subject)
                setText(htmlContent, true)
            }
        }

        mailSender.send(mimeMessage)
    }
}
