package spring.webmvc.infrastructure.common

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class JsonSupport(
    private val objectMapper: ObjectMapper,
) {
    private val log = LoggerFactory.getLogger(JsonSupport::class.java)

    fun <T> readValue(json: String, clazz: Class<T>) =
        runCatching { objectMapper.readValue(json, clazz) }
            .onFailure { log.warn("Failed to deserialize [{}] to [{}]: {}", json, clazz.simpleName, it.message) }
            .getOrNull()

    fun writeValueAsString(obj: Any) =
        runCatching { objectMapper.writeValueAsString(obj) }
            .onFailure { log.warn("Failed to serialize [{}]: {}", obj::class.simpleName, it.message) }
            .getOrNull()
}