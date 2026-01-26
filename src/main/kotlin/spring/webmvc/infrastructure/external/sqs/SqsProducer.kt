package spring.webmvc.infrastructure.external.sqs

import com.fasterxml.jackson.databind.ObjectMapper
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.springframework.stereotype.Component

@Component
class SqsProducer(
    private val sqsTemplate: SqsTemplate,
    private val objectMapper: ObjectMapper,
) {
    fun <T : Any> send(
        queueName: String,
        payload: T,
        headers: Map<String, String> = emptyMap(),
    ) {
        sqsTemplate.send {
            it.queue(queueName)
                .payload(objectMapper.writeValueAsString(payload))
                .apply { headers.forEach {
                    (key, value) -> it.header(key, value) }
                }
        }
    }
}
