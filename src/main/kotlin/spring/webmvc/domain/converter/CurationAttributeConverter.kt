package spring.webmvc.domain.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component
import spring.webmvc.domain.model.vo.CurationAttribute

@Component
@Converter
class CurationAttributeConverter(
    private val objectMapper: ObjectMapper,
) : AttributeConverter<CurationAttribute, String?> {
    override fun convertToDatabaseColumn(attribute: CurationAttribute): String? =
        objectMapper.writeValueAsString(attribute)

    override fun convertToEntityAttribute(dbData: String?): CurationAttribute =
        dbData?.let { objectMapper.readValue<CurationAttribute>(it) }
            ?: CurationAttribute(tagIds = emptyList())
}
