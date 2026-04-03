package spring.webmvc.infrastructure.external.ses

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.ses.model.*
import spring.webmvc.infrastructure.exception.FailedAwsException
import spring.webmvc.infrastructure.properties.AppProperties
import java.nio.charset.StandardCharsets

@Component
class EmailSender(
    private val sesClient: SesClient,
    private val templateEngine: TemplateEngine,
    appProperties: AppProperties,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val senderEmail = appProperties.aws.ses.senderEmail

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

        val request = SendEmailRequest.builder()
            .source(senderEmail)
            .destination(Destination.builder().toAddresses(to).build())
            .message(
                Message.builder()
                    .subject(
                        Content.builder()
                            .charset(StandardCharsets.UTF_8.name())
                            .data(subject)
                            .build()
                    )
                    .body(
                        Body.builder()
                            .html(
                                Content.builder()
                                    .charset(StandardCharsets.UTF_8.name())
                                    .data(htmlContent)
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .build()

        try {
            val response = sesClient.sendEmail(request)
            log.info(
                "Email sent successfully: to={}, subject={}, messageId={}, variables={}",
                to, subject, response.messageId(), variables,
            )
        } catch (e: SesException) {
            log.error(
                "Email sending failed: to={}, subject={}, error={}, variables={}",
                to, subject, e.awsErrorDetails().errorMessage(), variables,
            )
            throw FailedAwsException(serviceName = e.awsErrorDetails().serviceName(), throwable = e)
        }
    }
}
