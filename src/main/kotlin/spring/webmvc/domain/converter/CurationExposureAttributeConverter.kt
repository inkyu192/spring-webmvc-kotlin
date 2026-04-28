package spring.webmvc.domain.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component
import spring.webmvc.domain.model.enums.CurationLayout
import spring.webmvc.domain.model.vo.CurationExposureAttribute

@Component
@Converter
class CurationExposureAttributeConverter(
    private val objectMapper: ObjectMapper,
) : AttributeConverter<CurationExposureAttribute, String?> {
    override fun convertToDatabaseColumn(attribute: CurationExposureAttribute): String =
        objectMapper.writeValueAsString(attribute)

    override fun convertToEntityAttribute(dbData: String?): CurationExposureAttribute =
        dbData?.let { objectMapper.readValue<CurationExposureAttribute>(it) }
            ?: CurationExposureAttribute(layout = CurationLayout.CAROUSEL)
}
