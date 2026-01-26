package spring.webmvc.application.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import spring.webmvc.application.dto.command.PasswordResetEmailCommand
import spring.webmvc.application.dto.command.VerifyEmailCommand
import spring.webmvc.application.enums.EmailTemplate
import spring.webmvc.infrastructure.external.sqs.SqsProducer

@Service
class EmailService(
    private val sqsProducer: SqsProducer,
    @Value("\${spring.cloud.aws.sqs.queues.email}") private val queueName: String,
) {
    fun sendVerifyEmail(command: VerifyEmailCommand) {
        sqsProducer.send(
            queueName = queueName,
            payload = command,
            headers = mapOf("emailTemplate" to EmailTemplate.JOIN_VERIFY.name),
        )
    }

    fun sendPasswordResetEmail(command: PasswordResetEmailCommand) {
        sqsProducer.send(
            queueName = queueName,
            payload = command,
            headers = mapOf("emailTemplate" to EmailTemplate.PASSWORD_RESET.name),
        )
    }
}
