package spring.webmvc.application.consumer

import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import spring.webmvc.application.enums.EmailTemplate
import spring.webmvc.application.strategy.email.EmailStrategy

@Component
class EmailConsumer(
    emailStrategies: List<EmailStrategy>,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val emailStrategyMap: Map<EmailTemplate, EmailStrategy>

    init {
        val duplicates = emailStrategies
            .groupBy { it.emailTemplate() }
            .filter { it.value.size > 1 }
            .keys

        check(duplicates.isEmpty()) { "중복된 EmailStrategy가 존재합니다: $duplicates" }

        emailStrategyMap = emailStrategies.associateBy { it.emailTemplate() }
    }

    @SqsListener("\${spring.cloud.aws.sqs.queues.email}")
    fun receive(
        @Header(required = false) emailTemplate: String?,
        @Payload payload: String,
    ) {
        try {
            requireNotNull(emailTemplate) { "emailTemplate header is null" }

            val template = EmailTemplate.valueOf(emailTemplate)
            val strategy = checkNotNull(emailStrategyMap[template]) { "구현되지 않은 이메일 템플릿: $template" }

            strategy.handle(payload)
        } catch (e: Exception) {
            logger.error("이메일 처리 실패: template=$emailTemplate, payload=$payload", e)
        }
    }
}
